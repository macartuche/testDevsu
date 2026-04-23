package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import ec.gob.loja.devsu.backend.domain.repository.MovimientoRepository;
import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import ec.gob.loja.devsu.backend.dto.ReporteResponseDTO;
import ec.gob.loja.devsu.backend.mapper.MovimientoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private MovimientoMapper movimientoMapper;

    @InjectMocks
    private ReporteServiceImpl service;

    private Movimiento mockMovimiento;
    private MovimientoDTO mockMovimientoDto;

    @BeforeEach
    void setUp() {
        mockMovimiento = new Movimiento();
        mockMovimiento.setId(1L);
        mockMovimiento.setFecha(LocalDateTime.of(2024, 1, 15, 10, 0));
        mockMovimiento.setTipoMovimiento("Deposito");
        mockMovimiento.setValor(new BigDecimal("100.00"));
        mockMovimiento.setSaldo(new BigDecimal("200.00"));

        mockMovimientoDto = new MovimientoDTO(
                1L,
                LocalDateTime.of(2024, 1, 15, 10, 0),
                "Juan Perez",
                "478758",
                "Ahorros",
                new BigDecimal("100.00"),
                true,
                new BigDecimal("100.00"),
                new BigDecimal("200.00")
        );
    }

    @Test
    void generarReporte_shouldReturnMovimientosAndPdf() {
        when(movimientoRepository.findByCuenta_Cliente_IdAndFechaBetween(
                eq(100L), any(), any())).thenReturn(List.of(mockMovimiento));
        when(movimientoMapper.toDto(mockMovimiento)).thenReturn(mockMovimientoDto);

        ReporteResponseDTO result = service.generarReporte(100L, "2024-01-01", "2024-01-31");

        assertNotNull(result);
        assertEquals(1, result.movimientos().size());
    }

    @Test
    void generarReporte_withEmptyMovimientos_shouldReturnEmptyList() {
        when(movimientoRepository.findByCuenta_Cliente_IdAndFechaBetween(
                eq(100L), any(), any())).thenReturn(List.of());

        ReporteResponseDTO result = service.generarReporte(100L, "2024-01-01", "2024-01-31");

        assertNotNull(result);
        assertTrue(result.movimientos().isEmpty());
    }
}