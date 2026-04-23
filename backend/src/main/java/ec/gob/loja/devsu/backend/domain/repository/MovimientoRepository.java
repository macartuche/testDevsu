package ec.gob.loja.devsu.backend.domain.repository;

import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuenta_IdAndFechaBetweenOrderByFechaDesc(Long cuentaId, LocalDateTime startDate, LocalDateTime endDate);
    List<Movimiento> findByCuenta_Cliente_IdAndFechaBetween(Long clienteId, LocalDateTime startDate, LocalDateTime endDate);
}
