package GESTOR_CINEMAR_CENTER.DEV.config;

import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoUsuario;
import GESTOR_CINEMAR_CENTER.DEV.model.Administrador;
import GESTOR_CINEMAR_CENTER.DEV.model.CandyCombo;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.CodigoDescuento;
import GESTOR_CINEMAR_CENTER.DEV.repository.CandyComboRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.CodigoDescuentoRepository;
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
    private final CandyComboRepository candyComboRepository;
    private final CodigoDescuentoRepository codigoDescuentoRepository;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            EntityManager entityManager,
            CandyComboRepository candyComboRepository,
            CodigoDescuentoRepository codigoDescuentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
        this.candyComboRepository = candyComboRepository;
        this.codigoDescuentoRepository = codigoDescuentoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Ampliar columna tipo (antes ENUM sin SUPERADMIN) para no truncar el nuevo rol.
            entityManager.createNativeQuery(
                    "ALTER TABLE usuario MODIFY COLUMN tipo VARCHAR(20) NOT NULL"
            ).executeUpdate();

            // Normalizar disc_tipo (JPA). No pisar tipo=SUPERADMIN.
            entityManager.createNativeQuery(
                    "UPDATE usuario SET disc_tipo = 'ADMINISTRADOR' WHERE UPPER(COALESCE(tipo,'')) IN ('ADMINISTRADOR','SUPERADMIN') OR UPPER(COALESCE(disc_tipo,'')) LIKE 'ADMIN%' OR UPPER(COALESCE(disc_tipo,'')) = 'SUPERADMIN'"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "UPDATE usuario SET disc_tipo = 'CLIENTE' WHERE UPPER(COALESCE(tipo,'')) = 'CLIENTE' OR UPPER(COALESCE(disc_tipo,'')) LIKE 'CLI%'"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "UPDATE usuario SET disc_tipo = 'CLIENTE' WHERE disc_tipo IS NULL OR (disc_tipo != 'ADMINISTRADOR' AND disc_tipo != 'CLIENTE')"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "UPDATE usuario SET tipo = 'ADMINISTRADOR' WHERE disc_tipo = 'ADMINISTRADOR' AND (tipo IS NULL OR UPPER(tipo) NOT IN ('ADMINISTRADOR','SUPERADMIN'))"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "UPDATE usuario SET tipo = 'CLIENTE' WHERE disc_tipo = 'CLIENTE' AND (tipo IS NULL OR UPPER(tipo) != 'CLIENTE')"
            ).executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET estado = 'ACTIVO' WHERE estado IS NULL OR estado = ''").executeUpdate();
            entityManager.createNativeQuery("UPDATE usuario SET intentos_fallidos = 0 WHERE intentos_fallidos IS NULL").executeUpdate();
            entityManager.flush();
        } catch (Exception e) {
            System.err.println("Nota: inicialización de mantenimiento: " + e.getMessage());
        }

        // Crear o actualizar SUPERADMIN por defecto
        usuarioRepository.findByEmailIgnoreCase("admin@mail.com").ifPresentOrElse(
            admin -> {
                admin.setPassword(passwordEncoder.encode("123123"));
                admin.setEstado(EstadoUsuario.ACTIVO);
                admin.setTipo(TipoUsuario.SUPERADMIN);
                admin.setIntentosFallidos(0);
                usuarioRepository.save(admin);
                System.out.println("✓ SuperAdmin por defecto verificado/actualizado: admin@mail.com / 123123");
            },
            () -> {
                Administrador admin = new Administrador(
                    "Super",
                    "Admin",
                    "admin@mail.com",
                    passwordEncoder.encode("123123"),
                    "2235678912"
                );
                admin.setEstado(EstadoUsuario.ACTIVO);
                admin.setTipo(TipoUsuario.SUPERADMIN);
                usuarioRepository.save(admin);
                System.out.println("✓ SuperAdmin por defecto creado: admin@mail.com / 123123");
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
                    "2231559876"
                );
                cliente.setEstado(EstadoUsuario.ACTIVO);
                cliente.setTipo(TipoUsuario.CLIENTE);
                usuarioRepository.save(cliente);
                System.out.println("✓ Cliente por defecto creado: cliente@mail.com / 123123");
            }
        );

        if (candyComboRepository.count() == 0) {
            candyComboRepository.save(new CandyCombo(null, "Combo Clásico",
                    "Balde mediano + gaseosa mediana", 4500.0, "local_drink", true));
            candyComboRepository.save(new CandyCombo(null, "Combo Marcenter",
                    "Balde grande + gaseosa grande + golosinas", 6900.0, "fastfood", true));
            candyComboRepository.save(new CandyCombo(null, "Combo Duo",
                    "2 baldes medianos + 2 gaseosas + 1 golosina", 8200.0, "groups", true));
            candyComboRepository.save(new CandyCombo(null, "Combo Familiar",
                    "Balde XL + 3 gaseosas + 2 golosinas", 11500.0, "celebration", true));
            candyComboRepository.save(new CandyCombo(null, "Combo Nachos",
                    "Nachos con cheddar + gaseosa mediana", 5200.0, "restaurant", true));
            System.out.println("✓ Combos Candy Bar sembrados");
        }

        if (!codigoDescuentoRepository.existsByCodigoIgnoreCase("MARCENTER10")) {
            CodigoDescuento codigo = new CodigoDescuento();
            codigo.setCodigo("MARCENTER10");
            codigo.setTipo(TipoDescuento.PORCENTAJE);
            codigo.setValor(10.0);
            codigo.setActivo(true);
            codigo.setUsosMaximos(null);
            codigo.setUsosActuales(0);
            codigo.setDescripcion("10% off en tu compra (entradas + candy)");
            codigoDescuentoRepository.save(codigo);
            System.out.println("✓ Código MARCENTER10 sembrado (10%)");
        }
    }
}

