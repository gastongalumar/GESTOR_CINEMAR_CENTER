package GESTOR_CINEMAR_CENTER.DEV.dto.request.auth;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidEmailFormato;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidNombrePersona;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidTelefonoArgentino;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para registrar un nuevo usuario")
public class RegistroRequest {

    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @ValidNombrePersona
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    @ValidNombrePersona
    private String apellido;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    @NotBlank(message = "El email es obligatorio")
    @ValidEmailFormato
    @Size(max = 255, message = "El email no puede superar 255 caracteres")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "123456")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    @Schema(description = "Teléfono de contacto", example = "+54 9 351 1234567")
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    @ValidTelefonoArgentino
    private String telefono;
}
