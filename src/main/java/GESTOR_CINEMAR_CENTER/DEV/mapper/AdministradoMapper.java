package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.AdministradorResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import ch.qos.logback.core.model.ComponentModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")

public interface AdministradoMapper {
AdministradorResponseDTO toDTO(Administrador administrador);
Administrador toEntity(Administrador administrador);

}
