package DEV.mapper;

import DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import DEV.dto.response.pelicula.PeliculaPageResponse;
import DEV.dto.response.pelicula.PeliculaResponseDTO;
import DEV.model.Pelicula;
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
    void actualizarEntity(ActualizarPeliculaRequestDTO request, @MappingTarget Pelicula pelicula);

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

