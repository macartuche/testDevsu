package ec.gob.loja.devsu.backend.service;

import ec.gob.loja.devsu.backend.dto.ClienteCreateDTO;
import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {
    List<ClienteDTO> getAll();
    ClienteDTO getByClienteId(Long clienteId);
    ClienteDTO create(ClienteCreateDTO dto);
    ClienteDTO update(Long clienteId, ClienteCreateDTO dto);
    void delete(Long clienteId);
}
