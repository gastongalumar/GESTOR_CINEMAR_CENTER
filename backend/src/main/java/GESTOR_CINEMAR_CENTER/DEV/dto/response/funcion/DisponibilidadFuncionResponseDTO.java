package GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cupos de asientos de una función (solo conteos)")
public class DisponibilidadFuncionResponseDTO {

    @Schema(description = "ID de la función", example = "10")
    private Long funcionId;

    @Schema(description = "Capacidad total de la sala", example = "50")
    private Integer capacidad;

    @Schema(description = "Asientos con reserva activa", example = "12")
    private Integer asientosOcupados;

    @Schema(description = "Asientos con hold vigente", example = "3")
    private Integer asientosEnHold;

    @Schema(description = "Asientos seleccionables (capacidad - ocupados - holds)", example = "35")
    private Integer asientosDisponibles;
}
