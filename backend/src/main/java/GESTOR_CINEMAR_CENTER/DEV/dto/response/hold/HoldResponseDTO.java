package GESTOR_CINEMAR_CENTER.DEV.dto.response.hold;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoldResponseDTO {
    private Long id;
    private Long funcionId;
    private String etiqueta;
    private LocalDateTime expiresAt;
}
