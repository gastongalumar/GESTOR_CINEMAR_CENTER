package GESTOR_CINEMAR_CENTER.DEV.dto.request.sala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class CrearSalaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "Las filas son obligatorias")
    @Positive(message = "Las filas deben ser mayor a cero")
    private Integer filas;

    @NotNull(message = "Las columnas son obligatorias")
    @Positive(message = "Las columnas deben ser mayor a cero")
    private Integer columnas;


}
