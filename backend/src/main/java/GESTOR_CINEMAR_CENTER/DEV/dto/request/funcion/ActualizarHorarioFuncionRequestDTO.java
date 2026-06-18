package GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
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
@Schema(description = "DTO para actualizar el horario de una función")
public class ActualizarHorarioFuncionRequestDTO {

    @Schema(description = "Nuevo horario de la función", example = "2026-05-24T20:00:00")
    @NotNull(message = "El horario es obligatorio")
    @Future(message = "El horario debe ser futuro")
    private LocalDateTime horario;
}
