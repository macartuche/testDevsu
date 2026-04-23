package ec.gob.loja.devsu.backend.mapper;

import ec.gob.loja.devsu.backend.domain.entity.Cuenta;
import ec.gob.loja.devsu.backend.dto.CuentaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapper {
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "cliente.clienteId", target = "clienteId")
    CuentaDTO toDto(Cuenta entity);
}
