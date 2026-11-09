package GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaComboItemDTO {
    private Long comboId;
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
