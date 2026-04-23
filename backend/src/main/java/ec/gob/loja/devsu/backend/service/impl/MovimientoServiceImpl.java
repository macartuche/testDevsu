package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cuenta;
import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import ec.gob.loja.devsu.backend.domain.repository.CuentaRepository;
import ec.gob.loja.devsu.backend.domain.repository.MovimientoRepository;
import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import ec.gob.loja.devsu.backend.exception.CupoDiarioExcedidoException;
import ec.gob.loja.devsu.backend.exception.SaldoNoDisponibleException;
import ec.gob.loja.devsu.backend.mapper.MovimientoMapper;
import ec.gob.loja.devsu.backend.service.MovimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private static final BigDecimal LIMITE_DIARIO = new BigDecimal("1000.00");
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository, MovimientoMapper movimientoMapper) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoMapper = movimientoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> getAll() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoDTO create(MovimientoDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(dto.numeroCuenta())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + dto.numeroCuenta()));

        BigDecimal valorMovimiento = dto.movimiento();
        boolean isRetiro = valorMovimiento.compareTo(BigDecimal.ZERO) < 0;

        // Calcular saldo disponible usando el último movimiento (o saldo inicial)
        List<Movimiento> movimientos = cuenta.getId() != null ? 
            movimientoRepository.findByCuenta_IdAndFechaBetweenOrderByFechaDesc(
                cuenta.getId(), LocalDateTime.of(1970, 1, 1, 0, 0), LocalDateTime.now().plusDays(1)
            ) : List.of();

        BigDecimal saldoActual = movimientos.isEmpty() ? cuenta.getSaldoInicial() : movimientos.get(0).getSaldo();

        if (isRetiro) {
            BigDecimal valorAbsoluto = valorMovimiento.abs();

            if (saldoActual.compareTo(valorAbsoluto) < 0) {
                throw new SaldoNoDisponibleException("Saldo no disponible");
            }

            // Calcular cupo diario usado (Lambdas / Streams funcionales)
            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
            
            List<Movimiento> movimientosDiarios = movimientoRepository
                    .findByCuenta_IdAndFechaBetweenOrderByFechaDesc(cuenta.getId(), startOfDay, endOfDay);

            BigDecimal retirosHoy = movimientosDiarios.stream()
                    .filter(m -> m.getValor().compareTo(BigDecimal.ZERO) < 0)
                    .map(m -> m.getValor().abs())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (retirosHoy.add(valorAbsoluto).compareTo(LIMITE_DIARIO) > 0) {
                throw new CupoDiarioExcedidoException("Cupo diario Excedido");
            }
        }

        BigDecimal nuevoSaldo = saldoActual.add(valorMovimiento);

        Movimiento entity = new Movimiento();
        entity.setFecha(LocalDateTime.now());
        entity.setTipoMovimiento(isRetiro ? "Retiro" : "Deposito");
        entity.setValor(valorMovimiento);
        entity.setSaldo(nuevoSaldo);
        entity.setCuenta(cuenta);

        // Opcionalmente actualizar el saldo inicial de la cuenta (algunos prefieren mantener el histórico intocable)
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return movimientoMapper.toDto(movimientoRepository.save(entity));
    }


}
