package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class OcupacionFuncionDTO {
    private Long funcionId;
    private String peliculaNombre;
    private String salaNombre;
    private String horario; // ISO datetime
    private double precio;
    private int asientosOcupados;
    private int capacidad;
    private int pctOcupacion;
    private double totalVentas;
}