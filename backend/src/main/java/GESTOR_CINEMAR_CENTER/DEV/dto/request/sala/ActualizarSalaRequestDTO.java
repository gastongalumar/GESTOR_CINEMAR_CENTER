package GESTOR_CINEMAR_CENTER.DEV.dto.request.sala;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.CapacidadSalaValida;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.NotBlankIfPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@CapacidadSalaValida
public class ActualizarSalaRequestDTO {

    @NotBlankIfPresent(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Positive(message = "Las filas deben ser mayor a cero")
    @Max(value = 20, message = "La sala no puede tener más de 20 filas")
    private Integer filas;

    @Positive(message = "Las columnas deben ser mayor a cero")
    @Max(value = 16, message = "La sala no puede tener más de 16 columnas")
    private Integer columnas;
}
