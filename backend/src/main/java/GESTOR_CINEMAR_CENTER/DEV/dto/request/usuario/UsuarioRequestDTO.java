package GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidTelefonoArgentino;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "Solicitud para crear o actualizar un usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "El nombre solo puede contener letras, espacios y caracteres .'-")
    @Schema(description = "Nombre del usuario", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "El apellido solo puede contener letras, espacios y caracteres .'-")
    @Schema(description = "Apellido del usuario", example = "Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 255, message = "El email no puede superar 255 caracteres")
    @Schema(description = "Email del usuario", example = "juan.perez@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    @Schema(description = "Contraseña del usuario", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @ValidTelefonoArgentino
    @Schema(description = "Teléfono del usuario", example = "+54 9 351 1234567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;
}
