package GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComboSeleccionadoDTO {

    @NotNull
    @Positive
    private Long comboId;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
