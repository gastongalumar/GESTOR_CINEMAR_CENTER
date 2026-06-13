package GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
public class CrearReservaRequestDTO {


    @Schema(description = "ID del cliente", example = "5")
    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El id de cliente debe ser válido")
    private Long clienteId;

    @Schema(description = "ID de la función", example = "5")
    @NotNull(message = "La función es obligatoria")
    @Positive(message = "El id de función debe ser válido")
    private Long funcionId;


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