package GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una función")
public class FuncionResponseDTO {

    @Schema(description = "ID de la función", example = "1")
    private Long id;

    @Schema(description = "ID de la sala", example = "2")
    private Long salaId;

    @Schema(description = "Nombre de la sala", example = "Sala 3D")
    private String salaNombre;

    @Schema(description = "Cantidad de filas", example = "10")
    private Integer salaFilas;

    @Schema(description = "Cantidad de columnas", example = "15")
    private Integer salaColumnas;

    @Schema(description = "ID de la película", example = "5")
    private Long peliculaId;

    @Schema(description = "Nombre de la película", example = "Interstellar")
    private String peliculaNombre;

    @Schema(description = "Horario de la función")
    private LocalDateTime horario;

    @Schema(description = "Precio de la entrada", example = "2500")
    private Double precio;

    @Schema(description = "Capacidad total de la sala", example = "150")
    private Integer capacidad;

    @Schema(description = "Asientos con reserva activa", example = "12")
    private Integer asientosOcupados;

    @Schema(description = "Asientos con hold vigente", example = "3")
    private Integer asientosEnHold;

    @Schema(description = "Asientos seleccionables (capacidad - ocupados - holds)", example = "135")
    private Integer asientosDisponibles;
}