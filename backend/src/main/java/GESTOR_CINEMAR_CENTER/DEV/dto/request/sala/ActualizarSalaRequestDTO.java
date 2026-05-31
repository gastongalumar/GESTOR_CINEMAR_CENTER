package GESTOR_CINEMAR_CENTER.DEV.dto.request.sala;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarSalaRequestDTO {

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Positive(message = "Las filas deben ser mayor a cero")
    private Integer filas;

    @Positive(message = "Las columnas deben ser mayor a cero")
    private Integer columnas;


}
