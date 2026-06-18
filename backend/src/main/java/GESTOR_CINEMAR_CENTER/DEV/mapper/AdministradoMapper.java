package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.administrador.AdministradorResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface AdministradoMapper {
AdministradorResponseDTO toDTO(Administrador administrador);
Administrador toEntity(Administrador administrador);

}
