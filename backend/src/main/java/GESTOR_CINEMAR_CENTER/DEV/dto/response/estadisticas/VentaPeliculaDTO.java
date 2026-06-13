package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VentaPeliculaDTO {
    private Long peliculaId;
    private String peliculaNombre;
    private long totalReservas;
    private long totalEntradas;
    private double totalVentas;
}