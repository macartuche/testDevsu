package ec.gob.loja.devsu.backend.dto;

import java.math.BigDecimal;

public record CuentaDTO(
        Long id,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        Boolean estado,
        String clienteNombre,
        Long clienteId
) {}
