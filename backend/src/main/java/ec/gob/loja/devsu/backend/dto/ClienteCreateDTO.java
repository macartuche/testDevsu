package ec.gob.loja.devsu.backend.dto;

public record ClienteCreateDTO(
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        String contrasena,
        Boolean estado
) {}