package GESTOR_CINEMAR_CENTER.DEV.dto.request.auth;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidEmailFormato;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credenciales de inicio de sesión")
public class LoginRequest {

    @Schema(description = "Email del usuario", example = "cliente@email.com")
    @NotBlank(message = "El email es obligatorio")
    @ValidEmailFormato
    @Size(max = 255, message = "El email no puede superar 255 caracteres")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "123456")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
