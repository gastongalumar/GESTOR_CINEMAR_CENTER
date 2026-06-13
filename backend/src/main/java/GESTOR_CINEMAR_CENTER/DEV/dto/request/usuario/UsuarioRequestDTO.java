package GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Solicitud para crear o actualizar un usuario")
public class UsuarioRequestDTO {
    @NotBlank
    @Schema(description = "Nombre del usuario", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank
    @Schema(description = "Apellido del usuario", example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;

    @Email
    @Schema(description = "Email del usuario", example = "juan.perez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Schema(description = "Teléfono del usuario", example = "+34612345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;

}

