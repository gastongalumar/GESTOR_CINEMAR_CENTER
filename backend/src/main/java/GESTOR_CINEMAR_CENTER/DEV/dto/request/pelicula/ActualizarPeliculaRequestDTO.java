package GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.FechasPeliculaValidas;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.NotBlankIfPresent;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidGeneroPelicula;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
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
@FechasPeliculaValidas
@Schema(description = "DTO para actualizar una película")
public class ActualizarPeliculaRequestDTO {

    @Schema(description = "Nombre de la película", example = "Interstellar")
    @NotBlankIfPresent(message = "El nombre no puede estar vacío")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombre;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    @Positive(message = "La duración debe ser mayor a cero")
    @Max(value = 600, message = "La duración no puede superar 600 minutos")
    private Integer duracionMinutos;

    @Schema(description = "Género de la película", example = "DRAMA")
    @ValidGeneroPelicula
    private String genero;

    @Schema(description = "Sinopsis de la película", example = "Un grupo de exploradores viaja a través de un agujero de gusano en el espacio.")
    @Size(max = 2000, message = "La sinopsis no puede superar 2000 caracteres")
    private String sinopsis;

    @Schema(description = "Descripción detallada de la película")
    @Size(max = 5000, message = "La descripción no puede superar 5000 caracteres")
    private String descripcion;

    @Schema(description = "Director de la película", example = "Christopher Nolan")
    @Size(max = 200, message = "El director no puede superar 200 caracteres")
    private String director;

    @Schema(description = "Elenco principal", example = "Matthew McConaughey, Anne Hathaway")
    @Size(max = 500, message = "El elenco no puede superar 500 caracteres")
    private String elenco;

    @Schema(description = "Clasificación etaria", example = "ATP")
    @Size(max = 100, message = "La clasificación no puede superar 100 caracteres")
    private String clasificacion;

    @Schema(description = "Nominaciones o premios relevantes")
    @Size(max = 1000, message = "Las nominaciones no pueden superar 1000 caracteres")
    private String nominaciones;

    @Schema(description = "URL del tráiler (YouTube)", example = "https://www.youtube.com/watch?v=zSWdZVtFT9E")
    @Size(max = 500, message = "La URL del tráiler no puede superar 500 caracteres")
    private String urlTrailer;
}
