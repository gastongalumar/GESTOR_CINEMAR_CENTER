package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.PagoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.ReservaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.repository.CandyComboRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import GESTOR_CINEMAR_CENTER.DEV.service.CodigoDescuentoService;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import GESTOR_CINEMAR_CENTER.DEV.service.HoldService;
import GESTOR_CINEMAR_CENTER.DEV.service.PagoService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import GESTOR_CINEMAR_CENTER.DEV.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTicketOwnershipTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private FuncionRepository funcionRepository;
    @Mock private FuncionService funcionService;
    @Mock private PagoService pagoService;
    @Mock private UsuarioService usuarioService;
    @Mock private SalaService salaService;
    @Mock private AsientoService asientoService;
    @Mock private ReservaMapper reservaMapper;
    @Mock private PagoMapper pagoMapper;
    @Mock private HoldService holdService;
    @Mock private CandyComboRepository candyComboRepository;
    @Mock private CodigoDescuentoService codigoDescuentoService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        Cliente dueño = new Cliente();
        dueño.setId(1L);
        dueño.setEmail("dueno@test.com");

        Funcion funcion = new Funcion();
        funcion.setId(10L);
        funcion.setHorario(LocalDateTime.now().plusDays(3));

        reserva = new Reserva();
        reserva.setId(5L);
        reserva.setNumeroTicket("TK123");
        reserva.setCliente(dueño);
        reserva.setFuncion(funcion);

        when(reservaRepository.findByNumeroTicket("TK123")).thenReturn(Optional.of(reserva));
    }

    @Test
    void duenoPuedeObtenerTicket() {
        Reserva result = reservaService.obtenerTicketSiAutorizado("TK123", "dueno@test.com", false);
        assertEquals("TK123", result.getNumeroTicket());
    }

    @Test
    void clienteAjenoRecibe404Uniforme() {
        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> reservaService.obtenerTicketSiAutorizado("TK123", "otro@test.com", false));
        assertEquals("No encontramos ese ticket.", ex.getMessage());
    }

    @Test
    void staffPuedeObtenerTicketAjeno() {
        Reserva result = reservaService.obtenerTicketSiAutorizado("TK123", "admin@test.com", true);
        assertEquals("TK123", result.getNumeroTicket());
    }
}
