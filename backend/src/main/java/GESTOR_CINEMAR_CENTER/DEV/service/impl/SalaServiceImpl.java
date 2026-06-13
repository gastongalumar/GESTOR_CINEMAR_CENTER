package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.ActualizarSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.asiento.AsientoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.sala.SalaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.AsientoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.SalaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.SalaRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import GESTOR_CINEMAR_CENTER.DEV.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service("salaService")
public class SalaServiceImpl implements SalaService {

    private final SalaRepository salaRepository;
    private final AsientoService asientoService;
    private final SalaMapper salaMapper;
    private final AsientoMapper asientoMapper;
    private final FuncionRepository funcionRepository;

    @Override
    public List<SalaResponseDTO> listarTodas() {
        return salaMapper.toDTOList(
                salaRepository.findByActivaTrue()
        );
    }

    private Sala obtenerEntidadActiva(Long id) {
        return salaRepository.findByIdAndActivaTrue(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Sala no encontrada o inactiva", id)
                );
    }

    @Override
    public SalaResponseDTO buscarPorId(Long id) {
        return salaMapper.toDTO(obtenerEntidadActiva(id));
    }

    @Override
    public SalaResponseDTO buscarPorNombre(String nombre) {
        Sala sala = salaRepository.findByNombreAndActivaTrue(nombre)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Sala", "nombre", nombre)
                );
        return salaMapper.toDTO(sala);
    }

    private void validarDimensiones(Integer filas, Integer columnas) {

        if (filas != null && filas > 26) {
            throw new ReglaNegocioException("La sala no puede tener más de 26 filas");
        }

        if (columnas != null && columnas > 15) {
            throw new ReglaNegocioException("La sala no puede tener más de 15 columnas");
        }

        if (filas != null && columnas != null && (filas * columnas) > 390) {
            throw new ReglaNegocioException("La capacidad máxima de la sala es de 390 asientos");
        }
    }

    @Override
    @Transactional
    public SalaResponseDTO crear(CrearSalaRequestDTO request) {

        if (salaRepository.existsByNombreAndActivaTrue(request.getNombre())) {
            throw new ReglaNegocioException(
                    "Ya existe una sala activa con el nombre: " + request.getNombre()
            );
        }

        validarDimensiones(request.getFilas(), request.getColumnas());

        Sala sala = salaMapper.toEntity(request);

        sala.setCapacidad(request.getFilas() * request.getColumnas());
        sala.setActiva(true);

        sala = salaRepository.save(sala);

        generarAsientos(sala);

        return salaMapper.toDTO(sala);
    }

    @Override
    @Transactional
    public SalaResponseDTO actualizar(Long id, ActualizarSalaRequestDTO request) {

        Sala existente = obtenerEntidadActiva(id);

        if (request.getNombre() != null
                && !existente.getNombre().equals(request.getNombre())
                && salaRepository.existsByNombreAndActivaTrue(request.getNombre())) {

            throw new ReglaNegocioException(
                    "Ya existe una sala activa con el nombre: " + request.getNombre()
            );
        }

        Integer nuevasFilas = request.getFilas() != null
                ? request.getFilas()
                : existente.getFilas();

        Integer nuevasColumnas = request.getColumnas() != null
                ? request.getColumnas()
                : existente.getColumnas();

        validarDimensiones(nuevasFilas, nuevasColumnas);

        boolean layoutCambio =
                !existente.getFilas().equals(nuevasFilas)
                        || !existente.getColumnas().equals(nuevasColumnas);

        existente.setNombre(
                request.getNombre() != null ? request.getNombre() : existente.getNombre()
        );

        existente.setFilas(nuevasFilas);
        existente.setColumnas(nuevasColumnas);
        existente.setCapacidad(nuevasFilas * nuevasColumnas);

        salaRepository.save(existente);

        if (layoutCambio) {
            asientoService.eliminarPorSala(existente);
            generarAsientos(existente);
        }

        return salaMapper.toDTO(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Sala sala = obtenerEntidadActiva(id);

        boolean tieneFuncionesFuturas =
                funcionRepository.existsBySalaAndHorarioAfter(sala, LocalDateTime.now().plusDays(1));

        if (tieneFuncionesFuturas) {
            throw new ReglaNegocioException(
                    "No se puede desactivar la sala porque tiene funciones futuras asociadas"
            );
        }

        sala.setActiva(false);
        salaRepository.save(sala);
    }

    @Override
    public List<AsientoResponseDTO> listarAsientosPorSala(Long salaId) {
        Sala sala = obtenerEntidadActiva(salaId);
        asegurarAsientosDeSala(sala);
        return asientoMapper.toResponseList(asientoService.obtenerPorSala(sala));
    }

    @Override
    @Transactional
    public void asegurarAsientosDeSala(Sala sala) {
        if (!asientoService.existenAsientosEnSala(sala)) {
            generarAsientos(sala);
        }
    }

    private void generarAsientos(Sala sala) {

        int filas = sala.getFilas();
        int columnas = sala.getColumnas();

        List<Asiento> asientos = new ArrayList<>();

        for (int f = 1; f <= filas; f++) {
            for (int c = 1; c <= columnas; c++) {

                String etiqueta = (char) ('A' + f - 1) + String.valueOf(c);

                asientos.add(new Asiento(sala, f, c, etiqueta));
            }
        }

        asientoService.guardarTodos(asientos);
    }

    @Override
    public Sala obtenerEntidad(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala", id));
    }
}