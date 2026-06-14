package GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidMetodoPago;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarMetodoPagoRequestDTO {

    @NotBlank(message = "El método de pago es obligatorio")
    @ValidMetodoPago
    private String metodoPago;
}
