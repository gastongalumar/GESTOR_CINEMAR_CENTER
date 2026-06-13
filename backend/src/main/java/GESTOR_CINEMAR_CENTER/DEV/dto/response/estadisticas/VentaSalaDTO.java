package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VentaSalaDTO {
    private Long salaId;
    private String salaNombre;
    private int capacidad;
    private long totalFunciones;
    private long totalEntradas;
    private int pctOcupacion; // 0-100
    private double totalVentas;
}