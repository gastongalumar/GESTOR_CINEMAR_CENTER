package GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.RangoFuncionesValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RangoFuncionesValido
@Schema(description = "DTO para crear funciones consecutivas en una sala dentro de un rango de fechas")
public class CrearFuncionesPorRangoRequestDTO {

    @Schema(description = "ID de la sala", example = "1")
    @NotNull(message = "La sala es obligatoria")
    @Positive(message = "El id de sala debe ser válido")
    private Long salaId;

    @Schema(description = "ID de la película", example = "5")
    @NotNull(message = "La película es obligatoria")
    @Positive(message = "El id de película debe ser válido")
    private Long peliculaId;

    @Schema(description = "Fecha de inicio del rango", example = "2026-06-15")
    @NotNull(message = "La fecha desde es obligatoria")
    @FutureOrPresent(message = "La fecha desde no puede ser pasada")
    private LocalDate fechaDesde;

    @Schema(description = "Fecha de fin del rango", example = "2026-06-25")
    @NotNull(message = "La fecha hasta es obligatoria")
    private LocalDate fechaHasta;

    @Schema(description = "Hora de inicio diaria de la primera función", example = "10:00:00")
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @Schema(description = "Hora límite diaria para programar funciones", example = "23:00:00")
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Schema(description = "Precio de cada función generada", example = "2500")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede ser mayor a 999999.99")
    @Digits(integer = 6, fraction = 2, message = "El precio debe tener como máximo 6 dígitos enteros y 2 decimales")
    private Double precio;
}
