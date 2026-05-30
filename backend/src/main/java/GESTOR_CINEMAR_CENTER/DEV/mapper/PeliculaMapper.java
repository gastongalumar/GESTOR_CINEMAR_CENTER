package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PeliculaMapper {

    @Mapping(target = "id", ignore = true)
    Pelicula toEntity(CrearPeliculaRequestDTO request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ActualizarPeliculaRequestDTO request, @MappingTarget Pelicula pelicula);

    PeliculaResponseDTO toResponse(Pelicula pelicula);

    List<PeliculaResponseDTO> toResponseList(List<Pelicula> peliculas);

    default PeliculaPageResponse toPageResponse(Page<Pelicula> page) {
        if (page == null) {
            return null;
        }
        PeliculaPageResponse response = new PeliculaPageResponse();
        response.setContent(toResponseList(page.getContent()));
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }
}

