package GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AsientoResponseDTO {

    private Long id;
    private Integer fila;
    private Integer columna;
    private String etiqueta;


}
