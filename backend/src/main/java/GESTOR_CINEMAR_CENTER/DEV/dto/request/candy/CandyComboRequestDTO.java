package GESTOR_CINEMAR_CENTER.DEV.dto.request.candy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Alta/edición de combo Candy Bar")
public class CandyComboRequestDTO {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    @DecimalMin("1.0")
    @DecimalMax("999999.99")
    private Double precio;

    @Size(max = 40)
    private String icono;

    private Boolean activo = Boolean.TRUE;
}
