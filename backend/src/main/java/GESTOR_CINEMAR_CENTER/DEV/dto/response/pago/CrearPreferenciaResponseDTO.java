package GESTOR_CINEMAR_CENTER.DEV.dto.response.pago;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearPreferenciaResponseDTO {
    @JsonProperty("init_point")
    private String initPoint;

    @JsonProperty("sandbox_init_point")
    private String sandboxInitPoint;

    @JsonProperty("preference_id")
    private String preferenceId;
}
