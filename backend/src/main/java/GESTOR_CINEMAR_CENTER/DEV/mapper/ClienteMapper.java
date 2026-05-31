package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.cliente.ClienteResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    ClienteResponseDTO toDTO(Cliente cliente);
    Cliente toEntity(Cliente cliente);

}
