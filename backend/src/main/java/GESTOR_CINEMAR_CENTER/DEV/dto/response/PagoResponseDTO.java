package GESTOR_CINEMAR_CENTER.DEV.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PagoResponseDTO {

    private Long id;
    private String numeroTicket;
    private Double monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPago;
    private String transaccionId;


}
