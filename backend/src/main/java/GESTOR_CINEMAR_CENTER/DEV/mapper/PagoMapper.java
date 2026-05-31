package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoResponseDTO toDTO(Pago pago);

    Pago toEntity(PagoResponseDTO pagoResponseDTO);

    List<PagoResponseDTO> toDTOList(List<Pago> pagos);
}
