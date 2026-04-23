package ec.gob.loja.devsu.backend.service;

import ec.gob.loja.devsu.backend.dto.CuentaDTO;
import java.util.List;

public interface CuentaService {
    List<CuentaDTO> getAll();
    CuentaDTO getByNumeroCuenta(String numeroCuenta);
    CuentaDTO create(CuentaDTO dto);
    CuentaDTO update(String numeroCuenta, CuentaDTO dto);
    void delete(String numeroCuenta);
}
