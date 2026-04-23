package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.domain.entity.Cuenta;
import ec.gob.loja.devsu.backend.domain.repository.ClienteRepository;
import ec.gob.loja.devsu.backend.domain.repository.CuentaRepository;
import ec.gob.loja.devsu.backend.dto.CuentaDTO;
import ec.gob.loja.devsu.backend.mapper.CuentaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaServiceImpl service;

    private Cliente mockCliente;
    private Cuenta mockCuenta;
    private CuentaDTO mockDto;

    @BeforeEach
    void setUp() {
        mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setClienteId(100L);
        mockCliente.setNombre("Juan Perez");

        mockCuenta = new Cuenta();
        mockCuenta.setId(1L);
        mockCuenta.setNumeroCuenta("478758");
        mockCuenta.setTipoCuenta("Ahorros");
        mockCuenta.setSaldoInicial(new BigDecimal("100.00"));
        mockCuenta.setEstado(true);
        mockCuenta.setCliente(mockCliente);

        mockDto = new CuentaDTO(1L, "478758", "Ahorros", new BigDecimal("100.00"), true, "Juan Perez", 100L);
    }

    @Test
    void getAll_shouldReturnAllCuentas() {
        when(cuentaRepository.findAll()).thenReturn(List.of(mockCuenta));
        when(cuentaMapper.toDto(mockCuenta)).thenReturn(mockDto);

        List<CuentaDTO> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("478758", result.get(0).numeroCuenta());
    }

    @Test
    void getByNumeroCuenta_whenExists_shouldReturnCuenta() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(mockCuenta));
        when(cuentaMapper.toDto(mockCuenta)).thenReturn(mockDto);

        CuentaDTO result = service.getByNumeroCuenta("478758");

        assertNotNull(result);
        assertEquals("478758", result.numeroCuenta());
    }

    @Test
    void getByNumeroCuenta_whenNotExists_shouldThrowException() {
        when(cuentaRepository.findByNumeroCuenta("999999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getByNumeroCuenta("999999"));
    }

    @Test
    void create_shouldSaveAndReturnCuenta() {
        when(clienteRepository.findByClienteId(100L)).thenReturn(Optional.of(mockCliente));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(mockCuenta);
        when(cuentaMapper.toDto(mockCuenta)).thenReturn(mockDto);

        CuentaDTO result = service.create(mockDto);

        assertNotNull(result);
    }

    @Test
    void create_whenClienteNotExists_shouldThrowException() {
        when(clienteRepository.findByClienteId(999L)).thenReturn(Optional.empty());

        CuentaDTO dto = new CuentaDTO(null, "478758", "Ahorros", new BigDecimal("100.00"), true, null, 999L);

        assertThrows(IllegalArgumentException.class, () -> service.create(dto));
    }

    @Test
    void delete_whenCuentaExists_shouldDelete() {
        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(mockCuenta));

        service.delete("478758");

        verify(cuentaRepository).delete(mockCuenta);
    }
}