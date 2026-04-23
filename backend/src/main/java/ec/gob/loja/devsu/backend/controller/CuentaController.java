package ec.gob.loja.devsu.backend.controller;

import ec.gob.loja.devsu.backend.dto.CuentaDTO;
import ec.gob.loja.devsu.backend.service.CuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> getAll() {
        return ResponseEntity.ok(cuentaService.getAll());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> getByNumeroCuenta(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(cuentaService.getByNumeroCuenta(numeroCuenta));
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> create(@RequestBody CuentaDTO dto) {
        return new ResponseEntity<>(cuentaService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> update(@PathVariable String numeroCuenta, @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(cuentaService.update(numeroCuenta, dto));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> delete(@PathVariable String numeroCuenta) {
        cuentaService.delete(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
