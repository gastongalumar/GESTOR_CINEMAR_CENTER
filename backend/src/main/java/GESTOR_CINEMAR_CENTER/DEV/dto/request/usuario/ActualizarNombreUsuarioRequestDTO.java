package GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar nombre, apellido y teléfono del usuario autenticado")
public class ActualizarNombreUsuarioRequestDTO {

    @Schema(description = "Nuevo nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 20, message = "El nombre no puede superar 20 caracteres")
    @ValidNombrePersona
    private String nombre;

    @Schema(description = "Nuevo apellido del usuario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 20, message = "El apellido no puede superar 20 caracteres")
    @ValidNombrePersona
    private String apellido;

    @Schema(
            description = "Teléfono nacional opcional (área sin 0 + número sin 15). Ej. 0223 / 5678912 → 2235678912. Vacío para borrarlo.",
            example = "2235678912"
    )
    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    @ValidTelefonoArgentino
    private String telefono;
}
