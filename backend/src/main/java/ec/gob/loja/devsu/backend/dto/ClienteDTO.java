package ec.gob.loja.devsu.backend.dto;

public record ClienteDTO(
        Long id,
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        Long clienteId,
        String contrasena,
        Boolean estado
) {}
