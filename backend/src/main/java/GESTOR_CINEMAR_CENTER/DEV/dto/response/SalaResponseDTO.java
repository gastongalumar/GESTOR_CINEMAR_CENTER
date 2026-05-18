package GESTOR_CINEMAR_CENTER.DEV.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SalaResponseDTO {

    private Long id;
    private String nombre;
    private Integer filas;
    private Integer columnas;
    private Integer capacidad;

}
