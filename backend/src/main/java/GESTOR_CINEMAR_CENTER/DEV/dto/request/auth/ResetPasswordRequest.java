package GESTOR_CINEMAR_CENTER.DEV.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Restablecer contraseña con token de recuperación")
public class ResetPasswordRequest {

    @Schema(description = "Token recibido por el enlace de recuperación")
    @NotBlank(message = "El token es obligatorio")
    @Size(max = 64)
    private String token;

    @Schema(description = "Nueva contraseña", example = "123456")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String passwordNueva;
}
