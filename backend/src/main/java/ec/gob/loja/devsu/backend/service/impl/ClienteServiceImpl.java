package ec.gob.loja.devsu.backend.service.impl;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.domain.repository.ClienteRepository;
import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import ec.gob.loja.devsu.backend.service.ClienteService;
import ec.gob.loja.devsu.backend.mapper.ClienteMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ClienteServiceImpl(ClienteRepository repository, ClienteMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO getByClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clienteId));
    }

    @Override
    @Transactional
    public ClienteDTO create(ClienteDTO dto) {
        Cliente entity = mapper.toEntity(dto);
        // Buena práctica: cifrar la contraseña con BCrypt
        if (entity.getContrasena() != null && !entity.getContrasena().isEmpty()) {
            entity.setContrasena(encoder.encode(entity.getContrasena()));
        }
        return mapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public ClienteDTO update(Long clienteId, ClienteDTO dto) {
        return repository.findByClienteId(clienteId).map(entity -> {
            mapper.updateEntity(dto, entity);
            // Buena práctica: cifrar la contraseña con BCrypt
            if (entity.getContrasena() != null && !entity.getContrasena().isEmpty()) {
                entity.setContrasena(encoder.encode(entity.getContrasena()));
            }
            return mapper.toDto(repository.save(entity));
        }).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + clienteId));
    }

    @Override
    @Transactional
    public void delete(Long clienteId) {
        repository.findByClienteId(clienteId).ifPresent(repository::delete);
    }


}
