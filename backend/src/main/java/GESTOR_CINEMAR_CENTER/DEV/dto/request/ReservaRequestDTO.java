package GESTOR_CINEMAR_CENTER.DEV.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear una reserva")
public class ReservaRequestDTO {

    @Schema(description = "ID del cliente", example = "1")
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @Schema(description = "ID de la función", example = "5")
    @NotNull(message = "La función es obligatoria")
    private Long funcionId;

    @Schema(description = "Monto total de la reserva", example = "7500")
    @NotNull(message = "El monto total es obligatorio")
    @Min(value = 1, message = "El monto total debe ser mayor a 0")
    private Double montoTotal;

    @Schema(description = "Método de pago", example = "Tarjeta de crédito")
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    @Schema(
            description = "Lista de asientos seleccionados",
            example = "[\"A1\", \"A2\", \"A3\"]"
    )
    @NotEmpty(message = "Debe seleccionar al menos un asiento")
    private List<String> asientosSeleccionados;
}