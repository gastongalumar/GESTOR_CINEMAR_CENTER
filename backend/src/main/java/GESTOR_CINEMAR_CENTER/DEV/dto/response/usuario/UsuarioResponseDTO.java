package GESTOR_CINEMAR_CENTER.DEV.dto.response.usuario;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private TipoUsuario tipo;
    private EstadoUsuario estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaUltimoAcceso;
}
