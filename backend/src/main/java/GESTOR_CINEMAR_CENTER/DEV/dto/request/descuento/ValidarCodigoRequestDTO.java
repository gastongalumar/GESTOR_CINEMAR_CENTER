package GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidarCodigoRequestDTO {

    @NotBlank
    private String codigo;

    /** Subtotal sobre el que se aplica el descuento (entradas + candy). */
    @NotNull
    @DecimalMin("0.0")
    private Double subtotal;
}
