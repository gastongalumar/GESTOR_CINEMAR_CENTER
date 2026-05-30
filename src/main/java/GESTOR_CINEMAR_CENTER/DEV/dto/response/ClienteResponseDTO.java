package GESTOR_CINEMAR_CENTER.DEV.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta de datos del cliente")
public class ClienteResponseDTO {
    @Schema(description = "ID del cliente", example = "1")
    private Integer id;

    @Schema(description = "Nombre del cliente", example = "María")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "López")
    private String apellido;

    @Schema(description = "Email del cliente", example = "maria@example.com")
    private String email;

    @Schema(description = "Puntos de fidelización acumulados", example = "500")
    private Integer puntosFidelidad;
}
