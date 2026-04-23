package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.domain.repository.ClienteRepository;
import ec.gob.loja.devsu.backend.dto.ClienteCreateDTO;
import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import ec.gob.loja.devsu.backend.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper mapper;

    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente mockCliente;
    private ClienteDTO mockDto;
    private ClienteCreateDTO mockCreateDto;

    @BeforeEach
    void setUp() {
        mockCliente = new Cliente();
        mockCliente.setId(1L);
        mockCliente.setClienteId(100L);
        mockCliente.setNombre("Juan Perez");
        mockCliente.setIdentificacion("1234567890");
        mockCliente.setGenero("M");
        mockCliente.setEdad(30);
        mockCliente.setTelefono("0991234567");
        mockCliente.setDireccion("Quito");
        mockCliente.setContrasena("password");
        mockCliente.setEstado(true);

        mockDto = new ClienteDTO(1L, "Juan Perez", "M", 30, "1234567890", "Quito", "0991234567", 100L, true);
        mockCreateDto = new ClienteCreateDTO("Juan Perez", "M", 30, "1234567890", "Quito", "0991234567", "password", true);
    }

    @Test
    void getAll_shouldReturnAllClientes() {
        when(repository.findAll()).thenReturn(List.of(mockCliente));
        when(mapper.toDto(mockCliente)).thenReturn(mockDto);

        List<ClienteDTO> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).nombre());
    }

    @Test
    void getByClienteId_whenClienteExists_shouldReturnCliente() {
        when(repository.findByClienteId(100L)).thenReturn(Optional.of(mockCliente));
        when(mapper.toDto(mockCliente)).thenReturn(mockDto);

        ClienteDTO result = service.getByClienteId(100L);

        assertNotNull(result);
        assertEquals("Juan Perez", result.nombre());
    }

    @Test
    void getByClienteId_whenClienteNotExists_shouldThrowException() {
        when(repository.findByClienteId(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getByClienteId(999L));
    }

    @Test
    void create_shouldSaveAndReturnCliente() {
        when(mapper.toEntityFromCreate(mockCreateDto)).thenReturn(mockCliente);
        when(mapper.toDto(mockCliente)).thenReturn(mockDto);
        when(repository.save(mockCliente)).thenReturn(mockCliente);

        ClienteDTO result = service.create(mockCreateDto);

        assertNotNull(result);
        verify(repository).save(mockCliente);
    }

    @Test
    void update_whenClienteExists_shouldUpdateAndReturn() {
        when(repository.findByClienteId(100L)).thenReturn(Optional.of(mockCliente));
        when(mapper.toDto(mockCliente)).thenReturn(mockDto);
        when(repository.save(mockCliente)).thenReturn(mockCliente);

        ClienteCreateDTO updateDto = new ClienteCreateDTO("Juan Actualizado", "M", 30, "1234567890", "Quito", "0988888888", "password", true);

        ClienteDTO result = service.update(100L, updateDto);

        assertNotNull(result);
    }

    @Test
    void update_whenClienteNotExists_shouldThrowException() {
        when(repository.findByClienteId(999L)).thenReturn(Optional.empty());

        ClienteCreateDTO updateDto = new ClienteCreateDTO("Juan", "M", 30, "1234567890", "Quito", "0991234567", "pass", true);
        assertThrows(IllegalArgumentException.class, () -> service.update(999L, updateDto));
    }

    @Test
    void delete_whenClienteExists_shouldDelete() {
        when(repository.findByClienteId(100L)).thenReturn(Optional.of(mockCliente));

        service.delete(100L);

        verify(repository).delete(mockCliente);
    }
}