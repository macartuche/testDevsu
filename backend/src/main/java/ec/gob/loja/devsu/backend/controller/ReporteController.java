package ec.gob.loja.devsu.backend.controller;

import ec.gob.loja.devsu.backend.dto.ReporteResponseDTO;
import ec.gob.loja.devsu.backend.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public ResponseEntity<ReporteResponseDTO> generarReporte(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {
        return ResponseEntity.ok(reporteService.generarReporte(clienteId, fechaInicio, fechaFin));
    }
}
