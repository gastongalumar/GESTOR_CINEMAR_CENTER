package GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.UniqueElements;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidAsientoEtiqueta;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidMetodoPago;
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

    @Schema(description = "ID de la función", example = "5")
    @NotNull(message = "La función es obligatoria")
    @Positive(message = "El id de función debe ser válido")
    private Long funcionId;

    @Schema(description = "Método de pago", example = "TARJETA")
    @NotBlank(message = "El método de pago es obligatorio")
    @ValidMetodoPago
    private String metodoPago;

    @Schema(
            description = "Lista de asientos seleccionados",
            example = "[\"A1\", \"A2\", \"A3\"]"
    )
    @NotEmpty(message = "Debe seleccionar al menos un asiento")
    @Size(max = 20, message = "No se pueden reservar más de 20 asientos por operación")
    @UniqueElements(message = "No se pueden seleccionar asientos duplicados")
    private List<@ValidAsientoEtiqueta String> asientosSeleccionados;
}
