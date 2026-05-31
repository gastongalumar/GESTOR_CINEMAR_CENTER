package DEV.mapper;

import DEV.dto.response.PagoResponseDTO;
import DEV.model.Pago;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoResponseDTO toDTO(Pago pago);

    Pago toEntity(PagoResponseDTO pagoResponseDTO);

    List<PagoResponseDTO> toDTOList(List<Pago> pagos);
}
