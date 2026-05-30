package GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula;

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

    @Schema(description = "Ruta o URL de la imagen", example = "/imagenes/interstellar.jpg")
    private String rutaImagen;

    @Schema(description = "Fecha de estreno", example = "2026-05-20")
    private LocalDate fechaEstreno;

    @Schema(description = "Fecha de salida", example = "2026-06-20")
    private LocalDate fechaSalida;

    @Schema(description = "Duración en minutos", example = "169")
    private Integer duracionMinutos;
}
