package DEV.mapper;

import DEV.dto.response.AdministradorResponseDTO;
import DEV.model.Administrador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface AdministradoMapper {
AdministradorResponseDTO toDTO(Administrador administrador);
Administrador toEntity(Administrador administrador);

}
