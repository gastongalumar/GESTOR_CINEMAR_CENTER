package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas.*;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoPago;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pago;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.*;
import GESTOR_CINEMAR_CENTER.DEV.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements EstadisticasService {

    private static final Set<EstadoReserva> ESTADOS_RESERVA_EXCLUIDOS = Set.of(
            EstadoReserva.CANCELADA,
            EstadoReserva.EXPIRADA,
            EstadoReserva.REEMBOLSADA
    );

    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    @Override
    public DashboardResumenDTO obtenerResumen() {
        long totalReservas = reservaRepository.countReservasActivas() != null ? reservaRepository.countReservasActivas() : 0;
        long totalEntradas = reservaRepository.countEntradasTotales() != null ? reservaRepository.countEntradasTotales() : 0;
        double totalVentas = pagoRepository.sumTotalVentas() != null ? pagoRepository.sumTotalVentas() : 0;
        double ventasHoy = pagoRepository.sumVentasPorFecha(LocalDate.now()) != null ? pagoRepository.sumVentasPorFecha(LocalDate.now()) : 0;
        long totalPeliculas = peliculaRepository.countByActivaTrue();
        long totalFunciones = funcionRepository.countByActivaTrue();
        long funcionesVigentes = funcionRepository.countVigentes() != null ? funcionRepository.countVigentes() : 0;
        long totalSalas = salaRepository.countByActivaTrue();

        return new DashboardResumenDTO(
                totalReservas,
                totalEntradas,
                totalVentas,
                ventasHoy,
                totalPeliculas,
                totalFunciones,
                funcionesVigentes,
                totalSalas
        );
    }

    @Override
    public List<VentaDiariaDTO> ventasDiarias(int dias) {
        LocalDate fechaInicio = LocalDate.now().minusDays(dias);

        List<Pago> pagos = pagosContabilizables();

        Map<LocalDate, List<Pago>> pagosPorFecha = pagos.stream()
                .filter(p -> p.getFechaPago() != null && p.getFechaPago().toLocalDate().isAfter(fechaInicio.minusDays(1)))
                .collect(Collectors.groupingBy(p -> p.getFechaPago().toLocalDate()));

        List<VentaDiariaDTO> resultado = new ArrayList<>();

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(LocalDate.now()); fecha = fecha.plusDays(1)) {
            List<Pago> pagosDia = pagosPorFecha.getOrDefault(fecha, new ArrayList<>());

            long reservasDelDia = pagosDia.stream()
                    .map(Pago::getReserva)
                    .distinct()
                    .count();

            long entradasDelDia = pagosDia.stream()
                    .flatMap(p -> p.getReserva().getAsientos().stream())
                    .count();

            double ventasDelDia = pagosDia.stream()
                    .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                    .sum();

            resultado.add(new VentaDiariaDTO(
                    fecha.toString(),
                    reservasDelDia,
                    entradasDelDia,
                    ventasDelDia
            ));
        }

        return resultado;
    }

    @Override
    public List<VentaPeliculaDTO> ventasPorPelicula() {
        List<Pelicula> peliculas = peliculaRepository.findByActivaTrue();
        List<VentaPeliculaDTO> resultado = new ArrayList<>();

        for (Pelicula pelicula : peliculas) {
            List<Funcion> funcionesDePelicula = funcionRepository.findByActivaTrueAndPelicula(pelicula);

            long totalReservas = 0;
            long totalEntradas = 0;
            double totalVentas = 0;

            for (Funcion funcion : funcionesDePelicula) {
                List<Pago> pagosFuncion = pagosContabilizablesPorFuncion(funcion.getId());

                totalReservas += pagosFuncion.stream()
                        .map(Pago::getReserva)
                        .distinct()
                        .count();

                totalEntradas += pagosFuncion.stream()
                        .flatMap(p -> p.getReserva().getAsientos().stream())
                        .count();

                totalVentas += pagosFuncion.stream()
                        .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                        .sum();
            }

            resultado.add(new VentaPeliculaDTO(
                    pelicula.getId(),
                    pelicula.getNombre(),
                    totalReservas,
                    totalEntradas,
                    totalVentas
            ));
        }

        return resultado.stream()
                .filter(v -> v.getTotalVentas() > 0)
                .sorted(Comparator.comparingDouble(VentaPeliculaDTO::getTotalVentas).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<VentaSalaDTO> ventasPorSala() {
        List<Sala> salas = salaRepository.findByActivaTrue();
        List<VentaSalaDTO> resultado = new ArrayList<>();

        for (Sala sala : salas) {
            List<Funcion> funcionesDeSala = funcionRepository.findByActivaTrueAndSala(sala);

            long totalFunciones = funcionesDeSala.size();
            long totalEntradas = 0;
            double totalVentas = 0;
            int capacidad = sala.getCapacidad() != null ? sala.getCapacidad() : 0;

            for (Funcion funcion : funcionesDeSala) {
                List<Pago> pagosFuncion = pagosContabilizablesPorFuncion(funcion.getId());

                totalEntradas += pagosFuncion.stream()
                        .flatMap(p -> p.getReserva().getAsientos().stream())
                        .count();

                totalVentas += pagosFuncion.stream()
                        .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                        .sum();
            }

            int pctOcupacion = (totalFunciones > 0 && capacidad > 0)
                    ? (int) ((totalEntradas * 100) / (totalFunciones * capacidad))
                    : 0;

            resultado.add(new VentaSalaDTO(
                    sala.getId(),
                    sala.getNombre(),
                    capacidad,
                    totalFunciones,
                    totalEntradas,
                    pctOcupacion,
                    totalVentas
            ));
        }

        return resultado;
    }

    @Override
    public List<OcupacionFuncionDTO> ocupacionFunciones(boolean soloVigentes) {
        List<Funcion> funciones = soloVigentes
                ? funcionRepository.findByActivaTrueAndHorarioAfter(LocalDateTime.now())
                : funcionRepository.findByActivaTrue();

        List<OcupacionFuncionDTO> resultado = new ArrayList<>();

        for (Funcion funcion : funciones) {
            List<Pago> pagosFuncion = pagosContabilizablesPorFuncion(funcion.getId());

            int asientosOcupados = (int) pagosFuncion.stream()
                    .flatMap(p -> p.getReserva().getAsientos().stream())
                    .count();

            int capacidad = funcion.getSala().getCapacidad() != null ? funcion.getSala().getCapacidad() : 0;
            int pctOcupacion = (capacidad > 0) ? (asientosOcupados * 100) / capacidad : 0;

            double totalVentas = pagosFuncion.stream()
                    .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0)
                    .sum();

            resultado.add(new OcupacionFuncionDTO(
                    funcion.getId(),
                    funcion.getPelicula().getNombre(),
                    funcion.getSala().getNombre(),
                    funcion.getHorario().toString(),
                    funcion.getPrecio() != null ? funcion.getPrecio() : 0,
                    asientosOcupados,
                    capacidad,
                    pctOcupacion,
                    totalVentas
            ));
        }

        return resultado;
    }

    @Override
    public List<MetodoPagoDTO> metodosPago() {
        List<Pago> pagosValidos = pagosContabilizables();

        long totalPagos = pagosValidos.size();
        if (totalPagos == 0) {
            return new ArrayList<>();
        }

        Map<String, Long> conteoPorMetodo = pagosValidos.stream()
                .filter(p -> p.getMetodoPago() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getMetodoPago().toString(),
                        Collectors.counting()
                ));

        List<MetodoPagoDTO> resultado = new ArrayList<>();

        for (Map.Entry<String, Long> entry : conteoPorMetodo.entrySet()) {
            int porcentaje = (int) ((entry.getValue() * 100) / totalPagos);
            resultado.add(new MetodoPagoDTO(
                    entry.getKey(),
                    entry.getValue(),
                    porcentaje
            ));
        }

        return resultado.stream()
                .sorted(Comparator.comparingInt(MetodoPagoDTO::getPorcentaje).reversed())
                .collect(Collectors.toList());
    }

    private List<Pago> pagosContabilizables() {
        return pagoRepository.findAll().stream()
                .filter(this::esPagoContabilizable)
                .collect(Collectors.toList());
    }

    private List<Pago> pagosContabilizablesPorFuncion(Long funcionId) {
        return pagosContabilizables().stream()
                .filter(p -> p.getReserva().getFuncion().getId().equals(funcionId))
                .collect(Collectors.toList());
    }

    private boolean esReservaContabilizable(Reserva reserva) {
        return reserva != null && !ESTADOS_RESERVA_EXCLUIDOS.contains(reserva.getEstadoReserva());
    }

    private boolean esPagoContabilizable(Pago pago) {
        return pago != null
                && pago.getEstadoPago() == EstadoPago.COMPLETADO
                && esReservaContabilizable(pago.getReserva());
    }
}
