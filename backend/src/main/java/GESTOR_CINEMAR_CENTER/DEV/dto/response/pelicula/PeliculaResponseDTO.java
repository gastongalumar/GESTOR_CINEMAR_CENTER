package GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula;

import GESTOR_CINEMAR_CENTER.DEV.enums.GeneroPelicula;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de una película")
public class PeliculaResponseDTO {

    @Schema(description = "ID de la película", example = "1")
    private Long id;

    @Schema(description = "Nombre de la película", example = "Interstellar")
    private String nombre;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    private Integer duracionMinutos;

    @Schema(description = "Género de la película", example = "ACCION")
    private GeneroPelicula genero;

    @Schema(description = "Ruta de la imagen de la película", example = "/uploads/peliculas/abc123.jpg")
    private String rutaImagen;

    @Schema(description = "Sinopsis de la película", example = "Un grupo de exploradores viaja a través de un agujero de gusano en el espacio.")
    private String sinopsis;

    @Schema(description = "Descripción detallada de la película")
    private String descripcion;

    @Schema(description = "Director de la película", example = "Christopher Nolan")
    private String director;

    @Schema(description = "Elenco principal", example = "Matthew McConaughey, Anne Hathaway")
    private String elenco;

    @Schema(description = "Clasificación etaria", example = "ATP")
    private String clasificacion;

    @Schema(description = "Nominaciones o premios relevantes")
    private String nominaciones;

    @Schema(description = "URL del tráiler (YouTube)", example = "https://www.youtube.com/watch?v=zSWdZVtFT9E")
    private String urlTrailer;
}
