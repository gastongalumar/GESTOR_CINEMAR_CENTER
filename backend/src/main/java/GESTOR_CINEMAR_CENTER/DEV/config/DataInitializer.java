package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Normalizar disc_tipo y tipo a mayúsculas ('ADMINISTRADOR', 'CLIENTE') para evitar errores de discriminator en Hibernate
            entityManager.createNativeQuery("UPDATE usuario SET disc_tipo = 'ADMINISTRADOR' WHERE UPPER(disc_tipo) LIKE 'ADMIN%' OR UPPER(tipo) LIKE 'ADMIN%'").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET disc_tipo = 'CLIENTE' WHERE UPPER(disc_tipo) LIKE 'CLI%' OR UPPER(tipo) LIKE 'CLI%'").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET disc_tipo = 'CLIENTE' WHERE disc_tipo IS NULL OR (disc_tipo != 'ADMINISTRADOR' AND disc_tipo != 'CLIENTE')").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET tipo = 'ADMINISTRADOR' WHERE disc_tipo = 'ADMINISTRADOR'").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET tipo = 'CLIENTE' WHERE disc_tipo = 'CLIENTE'").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET estado = 'ACTIVO' WHERE estado IS NULL OR estado = ''").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET intentos_fallidos = 0 WHERE intentos_fallidos IS NULL").executeUpdate();
            entityManager.flush();
        } catch (Exception e) {
            System.err.println("Nota: inicialización de mantenimiento: " + e.getMessage());
        }

        // Crear o actualizar admin por defecto
        usuarioRepository.findByEmailIgnoreCase("admin@mail.com").ifPresentOrElse(
            admin -> {
                admin.setPassword(passwordEncoder.encode("123123"));
                admin.setEstado(EstadoUsuario.ACTIVO);
                admin.setTipo(TipoUsuario.ADMINISTRADOR);
                admin.setIntentosFallidos(0);
                usuarioRepository.save(admin);
                System.out.println("✓ Admin por defecto verificado/actualizado: admin@mail.com / 123123");
            },
            () -> {
                Administrador admin = new Administrador(
                    "Administrador",
                    "Sistema",
                    "admin@mail.com",
                    passwordEncoder.encode("123123"),
                    "+54 9 123456789"
                );
                admin.setEstado(EstadoUsuario.ACTIVO);
                admin.setTipo(TipoUsuario.ADMINISTRADOR);
                usuarioRepository.save(admin);
                System.out.println("✓ Admin por defecto creado: admin@mail.com / 123123");
            }
        );

        // Crear o actualizar cliente por defecto
        usuarioRepository.findByEmailIgnoreCase("cliente@mail.com").ifPresentOrElse(
            cliente -> {
                cliente.setPassword(passwordEncoder.encode("123123"));
                cliente.setEstado(EstadoUsuario.ACTIVO);
                cliente.setTipo(TipoUsuario.CLIENTE);
                cliente.setIntentosFallidos(0);
                usuarioRepository.save(cliente);
                System.out.println("✓ Cliente por defecto verificado/actualizado: cliente@mail.com / 123123");
            },
            () -> {
                Cliente cliente = new Cliente(
                    "Cliente",
                    "Prueba",
                    "cliente@mail.com",
                    passwordEncoder.encode("123123"),
                    "+54 9 987654321"
                );
                cliente.setEstado(EstadoUsuario.ACTIVO);
                cliente.setTipo(TipoUsuario.CLIENTE);
                usuarioRepository.save(cliente);
                System.out.println("✓ Cliente por defecto creado: cliente@mail.com / 123123");
            }
        );
    }
}

