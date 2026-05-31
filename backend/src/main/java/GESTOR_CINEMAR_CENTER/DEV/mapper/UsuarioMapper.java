package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.auth.AuthResponse;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MapperHelper.class)
public interface UsuarioMapper {

    @Mapping(source = "usuario", target = "nombre", qualifiedByName = "nombreCompleto")
    @Mapping(source = "usuario.tipo", target = "tipo", qualifiedByName = "enumName")
    @Mapping(source = "usuario.email", target = "email")
    @Mapping(source = "usuario.id", target = "id")
    @Mapping(source = "token", target = "token")
    AuthResponse toAuthResponse(Usuario usuario, String token);
}

