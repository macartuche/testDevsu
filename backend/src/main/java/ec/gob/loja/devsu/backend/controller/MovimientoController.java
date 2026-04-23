package ec.gob.loja.devsu.backend.controller;

import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import ec.gob.loja.devsu.backend.service.MovimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> getAll() {
        return ResponseEntity.ok(movimientoService.getAll());
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> create(@RequestBody MovimientoDTO dto) {
        return new ResponseEntity<>(movimientoService.create(dto), HttpStatus.CREATED);
    }
}
