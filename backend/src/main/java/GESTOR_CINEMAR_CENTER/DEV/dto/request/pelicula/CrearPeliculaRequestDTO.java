package GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o editar una película")
public class CrearPeliculaRequestDTO {

    @Schema(description = "Nombre de la película", example = "Interstellar")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombre;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    @NotNull(message = "La fecha de estreno es obligatoria")
    @Future(message = "La fecha de estreno debe ser futura")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracionMinutos;
}