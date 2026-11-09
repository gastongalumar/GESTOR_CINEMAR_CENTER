package GESTOR_CINEMAR_CENTER.DEV.dto.request.hold;

import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.ValidAsientoEtiqueta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearHoldRequestDTO {
    @NotNull
    @Positive
    private Long funcionId;

    @NotBlank
    @ValidAsientoEtiqueta
    private String etiqueta;
}
