package GESTOR_CINEMAR_CENTER.DEV.dto.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar el nombre y apellido del usuario autenticado")
public class ActualizarNombreUsuarioRequestDTO {

    @Schema(description = "Nuevo nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 20, message = "El nombre no puede superar 20 caracteres")
    @Pattern(
            regexp = "^(?!.*[.'-]{2})[\\p{L}\\s.'-]+$",
            message = "El apellido solo puede contener letras, espacios y un solo carácter especial seguido (. '-)")
    private String nombre;

    @Schema(description = "Nuevo apellido del usuario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 20, message = "El apellido no puede superar 20 caracteres")
    @Pattern(
            regexp = "^(?!.*[.'-]{2})[\\p{L}\\s.'-]+$",
            message = "El apellido solo puede contener letras, espacios y un solo carácter especial seguido (. '-)"
    )    private String apellido;
}
