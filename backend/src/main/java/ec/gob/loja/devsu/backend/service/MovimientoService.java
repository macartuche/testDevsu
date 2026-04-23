package ec.gob.loja.devsu.backend.service;

import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import java.util.List;

public interface MovimientoService {
    List<MovimientoDTO> getAll();
    MovimientoDTO create(MovimientoDTO dto);
}
