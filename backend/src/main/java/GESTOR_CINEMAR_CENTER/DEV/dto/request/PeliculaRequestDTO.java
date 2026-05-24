package GESTOR_CINEMAR_CENTER.DEV.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PeliculaRequestDTO {

    @Schema(description = "Nombre de la película", example = "Interstellar")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Ruta o URL de la imagen", example = "/imagenes/interstellar.jpg")
    @NotBlank(message = "La ruta de imagen es obligatoria")
    private String rutaImagen;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracionMinutos;
}