package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.ActualizarSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.AsientoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.SalaResponseDTO;

import GESTOR_CINEMAR_CENTER.DEV.exception.BusinessException;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.AsientoMapper;
import GESTOR_CINEMAR_CENTER.DEV.mapper.SalaMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.AsientoRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final AsientoRepository asientoRepository;
    private final SalaMapper salaMapper;
    private final AsientoMapper asientoMapper;

    public SalaService(SalaRepository salaRepository,
                       AsientoRepository asientoRepository,
                       SalaMapper salaMapper,
                       AsientoMapper asientoMapper) {
        this.salaRepository = salaRepository;
        this.asientoRepository = asientoRepository;
        this.salaMapper = salaMapper;
        this.asientoMapper = asientoMapper;
    }

    public List<SalaResponseDTO> listarTodas() {
        return salaMapper.toDTOList(salaRepository.findAll());
    }

    public SalaResponseDTO buscarPorId(Long id) {
        return salaMapper.toDTO(obtenerEntidad(id));
    }

    public SalaResponseDTO buscarPorNombre(String nombre) {
        Sala sala = salaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala", "nombre", nombre));
        return salaMapper.toDTO(sala);
    }

    @Transactional
    public SalaResponseDTO crear(CrearSalaRequestDTO request) {
        if (salaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una sala con el nombre: " + request.getNombre());
        }
        Sala sala = salaMapper.toEntity(request);
        sala = salaRepository.save(sala);
        generarAsientos(sala);
        return salaMapper.toDTO(sala);
    }

    @Transactional
    public SalaResponseDTO actualizar(Long id, ActualizarSalaRequestDTO request) {
        Sala existente = obtenerEntidad(id);
        if (request.getNombre() != null
                && !existente.getNombre().equals(request.getNombre())
                && salaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una sala con el nombre: " + request.getNombre());
        }

        boolean layoutCambio = request.getFilas() != null && request.getColumnas() != null
                && (!existente.getFilas().equals(request.getFilas())
                || !existente.getColumnas().equals(request.getColumnas()));

        salaMapper.updateEntity(request, existente);
        existente = salaRepository.save(existente);

        if (layoutCambio) {
            asientoRepository.deleteBySala(existente);
            generarAsientos(existente);
        }

        return salaMapper.toDTO(existente);
    }

    public void eliminar(Long id) {
        salaRepository.delete(obtenerEntidad(id));
    }

    public List<AsientoResponseDTO> listarAsientosPorSala(Long salaId) {
        Sala sala = obtenerEntidad(salaId);
        asegurarAsientosDeSala(sala);
        return asientoMapper.toResponseList(asientoRepository.findBySala(sala));
    }

    @Transactional
    public void asegurarAsientosDeSala(Sala sala) {
        if (!asientoRepository.existsBySala(sala)) {
            generarAsientos(sala);
        }
    }

    public Sala obtenerEntidad(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala", id));
    }

    private void generarAsientos(Sala sala) {
        int filas = sala.getFilas() != null ? sala.getFilas() : 10;
        int columnas = sala.getColumnas() != null ? sala.getColumnas() : 16;
        List<Asiento> asientos = new ArrayList<>();

        for (int f = 1; f <= filas; f++) {
            for (int c = 1; c <= columnas; c++) {
                String etiqueta = (char) ('A' + f - 1) + String.valueOf(c);
                asientos.add(new Asiento(sala, f, c, etiqueta));
            }
        }
        asientoRepository.saveAll(asientos);
    }
}
