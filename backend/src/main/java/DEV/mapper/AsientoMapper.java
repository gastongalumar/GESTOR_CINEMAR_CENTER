package DEV.mapper;

import DEV.dto.response.AsientoResponseDTO;

import DEV.model.Asiento;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AsientoMapper {

    AsientoResponseDTO toResponse(Asiento asiento);

    List<AsientoResponseDTO> toResponseList(List<Asiento> asientos);
}
