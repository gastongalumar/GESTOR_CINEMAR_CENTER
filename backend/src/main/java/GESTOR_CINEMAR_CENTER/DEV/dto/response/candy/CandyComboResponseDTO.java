package GESTOR_CINEMAR_CENTER.DEV.dto.response.candy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandyComboResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String icono;
    private Boolean activo;
}
