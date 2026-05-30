package GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar una película")
public class ActualizarPeliculaRequestDTO {

    @Schema(description = "Nombre de la película", example = "Interstellar")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombre;

    @Schema(description = "Ruta o URL de la imagen", example = "/imagenes/interstellar.jpg")
    private String rutaImagen;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    @Positive(message = "La duración debe ser mayor a cero")
    private Integer duracionMinutos;
}