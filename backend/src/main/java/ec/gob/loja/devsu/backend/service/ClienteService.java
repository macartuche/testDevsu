package ec.gob.loja.devsu.backend.service;

import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {
    List<ClienteDTO> getAll();
    ClienteDTO getByClienteId(Long clienteId);
    ClienteDTO create(ClienteDTO dto);
    ClienteDTO update(Long clienteId, ClienteDTO dto);
    void delete(Long clienteId);
}
