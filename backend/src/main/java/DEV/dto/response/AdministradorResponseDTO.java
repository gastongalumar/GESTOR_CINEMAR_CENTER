package DEV.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta de datos del administrador")
public class AdministradorResponseDTO {
    @Schema(description = "ID del administrador", example = "1")
    private Integer id;

    @Schema(description = "Nombre del administrador", example = "Carlos")
    private String nombre;

    @Schema(description = "Apellido del administrador", example = "García")
    private String apellido;

    @Schema(description = "Email del administrador", example = "admin@cinemar.com")
    private String email;

    @Schema(description = "Tipo de usuario", example = "ADMINISTRADOR")
    private String tipoUsuario;
}
