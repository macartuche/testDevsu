package ec.gob.loja.devsu.backend.service;

import ec.gob.loja.devsu.backend.dto.ReporteResponseDTO;

public interface ReporteService {
    ReporteResponseDTO generarReporte(Long clienteId, String fechaInicio, String fechaFin);
}
