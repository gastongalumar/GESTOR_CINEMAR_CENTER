package GESTOR_CINEMAR_CENTER.DEV.dto.request.pago;

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
public class CrearPreferenciaRequestDTO {
    @NotNull(message = "El id de reserva es obligatorio")
    @Positive(message = "El id de reserva debe ser positivo")
    private Long reservaId;
}
