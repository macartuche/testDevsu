package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.domain.entity.Cuenta;
import ec.gob.loja.devsu.backend.domain.repository.ClienteRepository;
import ec.gob.loja.devsu.backend.domain.repository.CuentaRepository;
import ec.gob.loja.devsu.backend.dto.CuentaDTO;
import ec.gob.loja.devsu.backend.service.CuentaService;
import ec.gob.loja.devsu.backend.mapper.CuentaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaMapper cuentaMapper;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, ClienteRepository clienteRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.clienteRepository = clienteRepository;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> getAll() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO getByNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .map(cuentaMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
    }

    @Override
    @Transactional
    public CuentaDTO create(CuentaDTO dto) {
        Cliente cliente = clienteRepository.findByClienteId(dto.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe con id: " + dto.clienteId()));

        Cuenta entity = new Cuenta();
        entity.setNumeroCuenta(dto.numeroCuenta());
        entity.setTipoCuenta(dto.tipoCuenta());
        entity.setSaldoInicial(dto.saldoInicial());
        entity.setEstado(dto.estado() != null ? dto.estado() : true);
        entity.setCliente(cliente);

        return cuentaMapper.toDto(cuentaRepository.save(entity));
    }

    @Override
    @Transactional
    public CuentaDTO update(String numeroCuenta, CuentaDTO dto) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta).map(entity -> {
            entity.setTipoCuenta(dto.tipoCuenta());
            if (dto.estado() != null) {
                entity.setEstado(dto.estado());
            }
            return cuentaMapper.toDto(cuentaRepository.save(entity));
        }).orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + numeroCuenta));
    }

    @Override
    @Transactional
    public void delete(String numeroCuenta) {
        cuentaRepository.findByNumeroCuenta(numeroCuenta).ifPresent(cuentaRepository::delete);
    }


}
