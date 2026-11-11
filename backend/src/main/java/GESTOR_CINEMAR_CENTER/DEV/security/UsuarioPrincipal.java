package GESTOR_CINEMAR_CENTER.DEV.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * UserDetails con la versión de sesión activa del usuario, usada para invalidar
 * JWTs emitidos en logins anteriores.
 */
public class UsuarioPrincipal extends User {

    private final Integer sessionVersion;

    public UsuarioPrincipal(String username,
                            String password,
                            Collection<? extends GrantedAuthority> authorities,
                            Integer sessionVersion) {
        super(username, password, authorities);
        this.sessionVersion = sessionVersion != null ? sessionVersion : 0;
    }

    public Integer getSessionVersion() {
        return sessionVersion;
    }
}
