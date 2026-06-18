package GESTOR_CINEMAR_CENTER.DEV.dto.response;

public class AuthResponse {

    private String token;
    private String tipo;
    private String email;
    private String nombre;
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
