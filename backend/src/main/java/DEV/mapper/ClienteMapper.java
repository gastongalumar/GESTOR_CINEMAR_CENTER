package DEV.mapper;

import DEV.dto.response.ClienteResponseDTO;
import DEV.model.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteResponseDTO toDTO(Cliente cliente);
    Cliente toEntity(Cliente cliente);

}
