package GESTOR_CINEMAR_CENTER.DEV.mapper;


import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FuncionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.horario", target = "horario")
    @Mapping(source = "request.precio", target = "precio")
    @Mapping(source = "sala", target = "sala")
    @Mapping(source = "pelicula", target = "pelicula")
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
