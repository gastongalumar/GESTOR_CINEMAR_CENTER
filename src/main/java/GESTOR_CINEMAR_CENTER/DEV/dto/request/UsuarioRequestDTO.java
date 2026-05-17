package GESTOR_CINEMAR_CENTER.DEV.dto.request;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UsuarioRequestDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotNull
    private String telefono;

}

