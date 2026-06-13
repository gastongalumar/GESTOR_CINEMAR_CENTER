package GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MetodoPagoDTO {
    private String metodo;   // EFECTIVO, TARJETA, MERCADO_PAGO, TRANSFERENCIA, etc.
    private long cantidad;   // cantidad de reservas/pagos
    private int porcentaje;  // 0-100
}