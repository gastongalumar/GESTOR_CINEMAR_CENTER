package GESTOR_CINEMAR_CENTER.DEV.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Solicitud de recuperación de contraseña")
public class ForgotPasswordRequest {

    @Schema(description = "Email de la cuenta", example = "cliente@mail.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 255)
    private String email;
}
