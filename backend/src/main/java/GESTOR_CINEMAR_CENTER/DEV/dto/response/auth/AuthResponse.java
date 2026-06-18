package GESTOR_CINEMAR_CENTER.DEV.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipo;

    @Schema(description = "Email del usuario autenticado", example = "cliente@email.com")
    private String email;

    @Schema(description = "Nombre del usuario autenticado", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "ID del usuario autenticado", example = "1")
    private Long id;

    public AuthResponse() {}

    public AuthResponse(String token, String tipo, String email, String nombre, Long id) {
        this.token = token;
        this.tipo = tipo;
        this.email = email;
        this.nombre = nombre;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
