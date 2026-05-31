package GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear una función")
public class CrearFuncionRequestDTO {

    @Schema(description = "ID de la sala", example = "1")
    @NotNull(message = "La sala es obligatoria")
    private Long salaId;

    @Schema(description = "ID de la película", example = "5")
    @NotNull(message = "La película es obligatoria")
    private Long peliculaId;

    @Schema(description = "Horario de la función", example = "2026-05-24T20:00:00")
    @NotNull(message = "El horario es obligatorio")
    @Future(message = "El horario debe ser futuro")
    private LocalDateTime horario;

    @Schema(description = "Precio de la entrada", example = "2500")
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Double precio;
}