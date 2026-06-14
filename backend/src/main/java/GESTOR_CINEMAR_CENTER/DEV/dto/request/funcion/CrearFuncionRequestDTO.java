package GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
    @Positive(message = "El id de sala debe ser válido")
    private Long salaId;

    @Schema(description = "ID de la película", example = "5")
    @NotNull(message = "La película es obligatoria")
    @Positive(message = "El id de película debe ser válido")
    private Long peliculaId;

    @Schema(description = "Horario de la función", example = "2026-05-24T20:00:00")
    @NotNull(message = "El horario es obligatorio")
    @Future(message = "El horario debe ser futuro")
    private LocalDateTime horario;

    @Schema(description = "Precio de la entrada", example = "2500")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede ser mayor a 999999.99")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener como máximo 6 dígitos enteros y 2 decimales")
    private Double precio;
}
