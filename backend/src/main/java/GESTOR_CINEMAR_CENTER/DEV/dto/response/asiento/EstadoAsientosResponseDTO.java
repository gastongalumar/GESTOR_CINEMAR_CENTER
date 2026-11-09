package GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoAsientosResponseDTO {
    private List<String> ocupados;
    private List<String> bloqueados;
    private List<String> misBloqueados;
}
