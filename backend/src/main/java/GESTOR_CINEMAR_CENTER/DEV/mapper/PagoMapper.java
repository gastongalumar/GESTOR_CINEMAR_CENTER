package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.PagoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MapperHelper.class)
public interface PagoMapper {

    @Mapping(source = "reserva.numeroTicket", target = "numeroTicket")
    @Mapping(source = "estadoPago", target = "estado", qualifiedByName = "enumName")
    @Mapping(source = "metodoPago", target = "metodoPago", qualifiedByName = "enumName")
    PagoResponseDTO toDTO(Pago pago);

    Pago toEntity(PagoResponseDTO pagoResponseDTO);

    List<PagoResponseDTO> toDTOList(List<Pago> pagos);
}
