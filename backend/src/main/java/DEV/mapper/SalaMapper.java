package DEV.mapper;


import DEV.dto.request.ActualizarSalaRequestDTO;
import DEV.dto.request.CrearSalaRequestDTO;
import DEV.dto.response.SalaResponseDTO;
import DEV.model.Sala;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalaMapper {


    SalaResponseDTO toDTO(Sala sala);

    Sala toEntity(CrearSalaRequestDTO crearSalaRequestDTO);

    List<SalaResponseDTO> toDTOList(List<Sala> salas);


    void updateEntity(ActualizarSalaRequestDTO request, Sala existente);
}
