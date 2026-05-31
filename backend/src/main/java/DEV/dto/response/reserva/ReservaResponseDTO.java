package DEV.dto.response.reserva;

import DEV.enums.EstadoReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una reserva")
public class ReservaResponseDTO {

    @Schema(description = "ID de la reserva", example = "1")
    private Long id;

    @Schema(description = "Número de ticket", example = "TK1748039201")
    private String numeroTicket;

    @Schema(description = "Código OR de la reserva", example = "OR-CMX-1748039201")
    private String codigoOR;

    @Schema(description = "ID del cliente", example = "1")
    private Long clienteId;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String clienteNombre;

    @Schema(description = "ID de la función", example = "5")
    private Long funcionId;

    @Schema(description = "Nombre de la película", example = "Interstellar")
    private String peliculaNombre;

    @Schema(description = "Horario de la función")
    private LocalDateTime horarioFuncion;

    @Schema(description = "Monto total de la reserva", example = "7500")
    private Double montoTotal;

    @Schema(description = "Método de pago", example = "Tarjeta de crédito")
    private String metodoPago;

    @Schema(description = "Fecha de emisión de la reserva")
    private LocalDateTime fechaEmision;

    @Schema(description = "Fecha de validación de la reserva")
    private LocalDateTime fechaValidacion;

    @Schema(description = "Estado de la reserva")
    private EstadoReserva estadoReserva;

    @Schema(
            description = "Asientos seleccionados",
            example = "[\"A1\", \"A2\", \"A3\"]"
    )
    private List<String> asientosSeleccionados;
}