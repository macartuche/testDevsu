package ec.gob.loja.devsu.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoDTO(
        Long id,
        LocalDateTime fecha,
        String cliente,
        String numeroCuenta,
        String tipo,
        BigDecimal saldoInicial,
        Boolean estado,
        BigDecimal movimiento,
        BigDecimal saldoDisponible
) {}
