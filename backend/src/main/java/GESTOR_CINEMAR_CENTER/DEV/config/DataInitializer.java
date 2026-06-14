package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear admin por defecto
        if (!usuarioRepository.existsByEmail("admin@mail.com")) {
            Administrador admin = new Administrador(
                "Administrador",
                "Sistema",
                "admin@mail.com",
                passwordEncoder.encode("123123"),
                "+54 9 123456789"
            );
            usuarioRepository.save(admin);
            System.out.println("✓ Admin por defecto creado: admin@mail.com / 123123");
        }

        // Crear cliente por defecto
        if (!usuarioRepository.existsByEmail("cliente@mail.com")) {
            Cliente cliente = new Cliente(
                "Cliente",
                "Prueba",
                "cliente@mail.com",
                passwordEncoder.encode("123123"),
                "+54 9 987654321"
            );
            usuarioRepository.save(cliente);
            System.out.println("✓ Cliente por defecto creado: cliente@mail.com / 123123");
        }
    }
}

