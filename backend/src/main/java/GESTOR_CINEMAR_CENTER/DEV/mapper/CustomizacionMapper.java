package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import GESTOR_CINEMAR_CENTER.DEV.model.Customizacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomizacionMapper {
    CustomizacionResponse toResponse(Customizacion entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CustomizacionRequest request, @MappingTarget Customizacion entity);
}
