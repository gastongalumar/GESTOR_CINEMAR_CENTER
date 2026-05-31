package GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionTicketResponseDTO {

    private String estado;
    private String numeroTicket;
    private String mensaje;
    private Double montoTotal;
    private List<String> asientos = new ArrayList<>();

}
