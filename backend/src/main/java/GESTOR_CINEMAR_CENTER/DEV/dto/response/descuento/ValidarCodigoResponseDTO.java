package GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidarCodigoResponseDTO {
    private boolean valido;
    private String mensaje;
    private String codigo;
    private TipoDescuento tipo;
    private Double valor;
    private Double descuentoMonto;
    private Double totalConDescuento;
}
