package GESTOR_CINEMAR_CENTER.DEV.service;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.mapper.FuncionMapper;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Pelicula;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.FuncionRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaService peliculaService;
    private final SalaService salaService;
    private final FuncionMapper funcionMapper;


    public List<Funcion> listarFuncionesEntity(){
        return funcionRepository.findAll();
    }
    public List<FuncionResponseDTO> listarTodas() {

        return funcionMapper.toResponseList(listarFuncionesEntity());
    }

    public List<Funcion> listarVigentesEntity(){
        return funcionRepository.findByHorarioAfter(LocalDateTime.now());
    }
    public List<FuncionResponseDTO> listarVigentes() {
        return funcionMapper.toResponseList(listarVigentesEntity());
    }

    public Funcion obtenerFuncion(Long id) {
        return funcionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Función:" + id));
    }

    public List<FuncionResponseDTO> listarPorPelicula(Long peliculaId) {
        Pelicula pelicula = peliculaService.obtenerPelicula(peliculaId);
        return funcionMapper.toResponseList(funcionRepository.findByPelicula(pelicula));
    }

    public FuncionResponseDTO buscarPorId(Long id) {
        return funcionMapper.toResponse(obtenerFuncion(id));
    }

    public FuncionResponseDTO crear(CrearFuncionRequestDTO request) {
        Sala sala = salaService.obtenerEntidad(request.getSalaId());

        Pelicula pelicula = peliculaService.obtenerPelicula(request.getPeliculaId());

        if (funcionRepository.existsBySalaAndHorario(sala, request.getHorario())) {
            throw new ReglaNegocioException("Ya existe una función en esa sala a esa hora");
        }

        Funcion funcion = funcionMapper.toEntity(request, sala, pelicula);
        return funcionMapper.toResponse(funcionRepository.save(funcion));
    }

    public void eliminar(Long id) {
        funcionRepository.delete(obtenerFuncion(id));
    }
}
