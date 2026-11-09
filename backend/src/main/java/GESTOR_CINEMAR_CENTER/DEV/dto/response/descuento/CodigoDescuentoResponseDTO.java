package GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento;

import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodigoDescuentoResponseDTO {
    private Long id;
    private String codigo;
    private TipoDescuento tipo;
    private Double valor;
    private Boolean activo;
    private Integer usosMaximos;
    private Integer usosActuales;
    private LocalDateTime vigenteDesde;
    private LocalDateTime vigenteHasta;
    private String descripcion;
}
