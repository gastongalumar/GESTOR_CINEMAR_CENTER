package GESTOR_CINEMAR_CENTER.DEV.dto.request.sala;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.CapacidadSalaValida;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@CapacidadSalaValida
public class CrearSalaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "Las filas son obligatorias")
    @Positive(message = "Las filas deben ser mayor a cero")
    @Max(value = 20, message = "La sala no puede tener más de 20 filas")
    @Digits(integer = 2, fraction = 0, message = "Las filas deben ser un número entero")
    private Integer filas;

    @NotNull(message = "Las columnas son obligatorias")
    @Positive(message = "Las columnas deben ser mayor a cero")
    @Max(value = 16, message = "La sala no puede tener más de 16 columnas")
    @Digits(integer = 2, fraction = 0, message = "Las columnas deben ser un número entero")
    private Integer columnas;
}
