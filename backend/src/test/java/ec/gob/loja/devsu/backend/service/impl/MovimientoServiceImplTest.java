package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.domain.entity.Cuenta;
import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import ec.gob.loja.devsu.backend.domain.repository.CuentaRepository;
import ec.gob.loja.devsu.backend.domain.repository.MovimientoRepository;
import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import ec.gob.loja.devsu.backend.exception.CupoDiarioExcedidoException;
import ec.gob.loja.devsu.backend.exception.SaldoNoDisponibleException;
import ec.gob.loja.devsu.backend.mapper.MovimientoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta mockCuenta;
    private Cliente mockCliente;

    @BeforeEach
    void setUp() {
        mockCliente = new Cliente();
        mockCliente.setNombre("Prueba Cliente");
        
        mockCuenta = new Cuenta();
        mockCuenta.setId(1L);
        mockCuenta.setNumeroCuenta("478758");
        mockCuenta.setTipoCuenta("Ahorros");
        mockCuenta.setSaldoInicial(new BigDecimal("2000.00"));
        mockCuenta.setCliente(mockCliente);
    }

    @Test
    void create_WhenRetiroExceedsBalance_ShouldThrowInsufficientBalanceException() {
        // Arrange
        MovimientoDTO dto = new MovimientoDTO(null, null, "Prueba Cliente", "478758", "Ahorros", null, true, new BigDecimal("-3000.00"), null);
        
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(mockCuenta));
        when(movimientoRepository.findByCuenta_IdAndFechaBetweenOrderByFechaDesc(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(SaldoNoDisponibleException.class, () -> movimientoService.create(dto));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void create_WhenRetiroExceedsDailyQuota_ShouldThrowDailyQuotaExceededException() {
        // Arrange
        MovimientoDTO dto = new MovimientoDTO(null, null, "Prueba Cliente", "478758", "Ahorros", null, true, new BigDecimal("-600.00"), null);
        
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(mockCuenta));
        
        Movimiento transaccionPasada = new Movimiento();
        transaccionPasada.setValor(new BigDecimal("-900.00")); // Hoy ya retiró 900
        transaccionPasada.setSaldo(new BigDecimal("1100.00"));
        
        // Simular que el saldo está bien, pero el cupo de hoy de 1000 se supera: 900 + 600 = 1500 > 1000
        when(movimientoRepository.findByCuenta_IdAndFechaBetweenOrderByFechaDesc(eq(1L), any(), any()))
                .thenReturn(List.of(transaccionPasada));

        // Act & Assert
        assertThrows(CupoDiarioExcedidoException.class, () -> movimientoService.create(dto));
        verify(movimientoRepository, never()).save(any());
    }
}
