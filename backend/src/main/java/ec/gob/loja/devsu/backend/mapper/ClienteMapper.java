package ec.gob.loja.devsu.backend.mapper;

import ec.gob.loja.devsu.backend.domain.entity.Cliente;
import ec.gob.loja.devsu.backend.dto.ClienteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    @Mapping(source = "id", target = "id")
    ClienteDTO toDto(Cliente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    Cliente toEntity(ClienteDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    void updateEntity(ClienteDTO dto, @MappingTarget Cliente entity);
}
