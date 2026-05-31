package DEV.mapper;


import DEV.dto.request.funcion.CrearFuncionRequestDTO;
import DEV.dto.response.funcion.FuncionResponseDTO;
import DEV.model.Funcion;
import DEV.model.Pelicula;
import DEV.model.Sala;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuncionMapper {

    @Mapping(target = "id", ignore = true)
    Funcion toEntity(CrearFuncionRequestDTO request, Sala sala, Pelicula pelicula);

    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "sala.nombre", target = "salaNombre")
    @Mapping(source = "sala.filas", target = "salaFilas")
    @Mapping(source = "sala.columnas", target = "salaColumnas")
    @Mapping(source = "pelicula.id", target = "peliculaId")
    @Mapping(source = "pelicula.nombre", target = "peliculaNombre")
    FuncionResponseDTO toResponse(Funcion funcion);

    List<FuncionResponseDTO> toResponseList(List<Funcion> funciones);
}
