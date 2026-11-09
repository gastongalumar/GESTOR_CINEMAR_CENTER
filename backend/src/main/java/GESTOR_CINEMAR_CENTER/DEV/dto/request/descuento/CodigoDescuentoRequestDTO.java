package GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Alta/edición de código de descuento")
public class CodigoDescuentoRequestDTO {

    @NotBlank
    @Size(max = 40)
    private String codigo;

    @NotNull
    private TipoDescuento tipo;

    @NotNull
    @DecimalMin("0.01")
    private Double valor;

    private Boolean activo = Boolean.TRUE;

    @Min(1)
    private Integer usosMaximos;

    private LocalDateTime vigenteDesde;
    private LocalDateTime vigenteHasta;

    @Size(max = 255)
    private String descripcion;
}
