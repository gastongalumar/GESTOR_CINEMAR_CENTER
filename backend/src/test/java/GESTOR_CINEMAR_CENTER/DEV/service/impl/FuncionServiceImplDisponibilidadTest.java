package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.DisponibilidadFuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.mapper.FuncionMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.AsientoHold;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.AsientoHoldRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import GESTOR_CINEMAR_CENTER.DEV.service.PeliculaService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionServiceImplDisponibilidadTest {

    @Mock
    private FuncionRepository funcionRepository;
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private AsientoHoldRepository asientoHoldRepository;
    @Mock
    private PeliculaService peliculaService;
    @Mock
    private SalaService salaService;
    @Mock
    private AsientoService asientoService;
    @Mock
    private FuncionMapper funcionMapper;

    @InjectMocks
    private FuncionServiceImpl funcionService;

    private Funcion funcion;

    @BeforeEach
    void setUp() {
        Sala sala = new Sala();
        sala.setId(1L);
        sala.setFilas(5);
        sala.setColumnas(10);
        sala.setCapacidad(50);

        funcion = new Funcion();
        funcion.setId(10L);
        funcion.setSala(sala);
        funcion.setActiva(true);

        when(funcionRepository.findByIdAndActivaTrue(10L)).thenReturn(Optional.of(funcion));
    }

    @Test
    void obtenerDisponibilidad_restaOcupadosYHolds() {
        when(reservaRepository.findByFuncionAndEstadoReservaIn(eq(funcion), any()))
                .thenReturn(List.of(reservaConAsientos(12)));
        when(asientoHoldRepository.findVigentesByFuncion(eq(funcion), any()))
                .thenReturn(holds(3, 100));

        DisponibilidadFuncionResponseDTO dto = funcionService.obtenerDisponibilidad(10L);

        assertEquals(10L, dto.getFuncionId());
        assertEquals(50, dto.getCapacidad());
        assertEquals(12, dto.getAsientosOcupados());
        assertEquals(3, dto.getAsientosEnHold());
        assertEquals(35, dto.getAsientosDisponibles());
    }

    @Test
    void obtenerDisponibilidad_pisoEnCeroCuandoAgotada() {
        when(reservaRepository.findByFuncionAndEstadoReservaIn(eq(funcion), any()))
                .thenReturn(List.of(reservaConAsientos(40)));
        when(asientoHoldRepository.findVigentesByFuncion(eq(funcion), any()))
                .thenReturn(holds(20, 200));

        DisponibilidadFuncionResponseDTO dto = funcionService.obtenerDisponibilidad(10L);

        assertEquals(0, dto.getAsientosDisponibles());
        assertEquals(40, dto.getAsientosOcupados());
        assertEquals(20, dto.getAsientosEnHold());
    }

    @Test
    void buscarPorId_enriqueceCuposEnDto() {
        when(reservaRepository.findByFuncionAndEstadoReservaIn(eq(funcion), any()))
                .thenReturn(List.of(reservaConAsientos(5)));
        when(asientoHoldRepository.findVigentesByFuncion(eq(funcion), any()))
                .thenReturn(holds(2, 300));

        FuncionResponseDTO base = new FuncionResponseDTO();
        base.setId(10L);
        when(funcionMapper.toResponse(funcion)).thenReturn(base);

        FuncionResponseDTO dto = funcionService.buscarPorId(10L);

        assertEquals(50, dto.getCapacidad());
        assertEquals(5, dto.getAsientosOcupados());
        assertEquals(2, dto.getAsientosEnHold());
        assertEquals(43, dto.getAsientosDisponibles());
    }

    private Reserva reservaConAsientos(int cantidad) {
        Reserva reserva = new Reserva();
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reserva.setAsientos(IntStream.rangeClosed(1, cantidad)
                .mapToObj(i -> asiento(i, "A" + i))
                .toList());
        return reserva;
    }

    private List<AsientoHold> holds(int cantidad, int idOffset) {
        return IntStream.rangeClosed(1, cantidad)
                .mapToObj(i -> {
                    AsientoHold hold = new AsientoHold();
                    hold.setId((long) (idOffset + i));
                    hold.setFuncion(funcion);
                    hold.setAsiento(asiento(idOffset + i, "H" + i));
                    return hold;
                })
                .toList();
    }

    private Asiento asiento(int id, String etiqueta) {
        Asiento asiento = new Asiento();
        asiento.setId((long) id);
        asiento.setEtiqueta(etiqueta);
        return asiento;
    }
}
