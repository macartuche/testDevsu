package ec.gob.loja.devsu.backend.mapper;

import ec.gob.loja.devsu.backend.domain.entity.Movimiento;
import ec.gob.loja.devsu.backend.dto.MovimientoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(source = "cuenta.cliente.nombre", target = "cliente")
    @Mapping(source = "cuenta.numeroCuenta", target = "numeroCuenta")
    @Mapping(source = "cuenta.tipoCuenta", target = "tipo")
    @Mapping(expression = "java(entity.getCuenta() != null ? entity.getCuenta().getSaldoInicial().subtract(entity.getValor()) : java.math.BigDecimal.ZERO)", target = "saldoInicial")
    @Mapping(source = "cuenta.estado", target = "estado")
    @Mapping(source = "valor", target = "movimiento")
    @Mapping(source = "saldo", target = "saldoDisponible")
    MovimientoDTO toDto(Movimiento entity);
}
