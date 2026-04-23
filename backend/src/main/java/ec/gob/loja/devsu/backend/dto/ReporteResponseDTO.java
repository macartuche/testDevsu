package ec.gob.loja.devsu.backend.dto;

import java.util.List;

public record ReporteResponseDTO(
        List<MovimientoDTO> movimientos,
        String pdfBase64
) {}
