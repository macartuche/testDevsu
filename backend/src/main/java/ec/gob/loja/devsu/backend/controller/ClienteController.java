package ec.gob.loja.devsu.backend.controller;

import ec.gob.loja.devsu.backend.dto.ClienteCreateDTO;
import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import ec.gob.loja.devsu.backend.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAll() {
        return ResponseEntity.ok(clienteService.getAll());
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> getByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(clienteService.getByClienteId(clienteId));
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> create(@RequestBody ClienteCreateDTO dto) {
        return new ResponseEntity<>(clienteService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> update(@PathVariable Long clienteId, @RequestBody ClienteCreateDTO dto) {
        return ResponseEntity.ok(clienteService.update(clienteId, dto));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> delete(@PathVariable Long clienteId) {
        clienteService.delete(clienteId);
        return ResponseEntity.noContent().build();
    }
}
