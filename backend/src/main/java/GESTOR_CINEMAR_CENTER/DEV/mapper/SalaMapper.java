package GESTOR_CINEMAR_CENTER.DEV.mapper;


import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.sala.SalaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalaMapper {


    SalaResponseDTO toDTO(Sala sala);

    Sala toEntity(CrearSalaRequestDTO crearSalaRequestDTO);

    List<SalaResponseDTO> toDTOList(List<Sala> salas);



}
