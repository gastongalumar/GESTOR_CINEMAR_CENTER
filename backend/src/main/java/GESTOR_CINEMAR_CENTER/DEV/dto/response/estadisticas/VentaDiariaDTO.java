package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VentaDiariaDTO {
    private String fecha;    // ISO date string, p.ej. "2026-06-12"
    private long reservas;
    private long entradas;
    private double ventas;
}