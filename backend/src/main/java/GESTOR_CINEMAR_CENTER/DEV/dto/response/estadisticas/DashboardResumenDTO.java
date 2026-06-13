package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DashboardResumenDTO {
    private long totalReservas;
    private long totalEntradas;
    private double totalVentas;
    private double ventasHoy;
    private long totalPeliculas;
    private long totalFunciones;
    private long funcionesVigentes;
    private long totalSalas;
}