package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.AsientoResponseDTO;

import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AsientoMapper {

    AsientoResponseDTO toResponse(Asiento asiento);

    List<AsientoResponseDTO> toResponseList(List<Asiento> asientos);
}
