package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionesPorRangoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;

import java.util.List;

public interface FuncionService {
    List<FuncionResponseDTO> listarTodas();
    List<FuncionResponseDTO> listarVigentes();
    Funcion obtenerFuncion(Long id);
    List<FuncionResponseDTO> listarPorPelicula(Long peliculaId);
    FuncionResponseDTO buscarPorId(Long id);
    FuncionResponseDTO crear(CrearFuncionRequestDTO request);
    List<FuncionResponseDTO> crearFuncionesPorRango(CrearFuncionesPorRangoRequestDTO request);
    void eliminar(Long id);
    boolean existeFuncionFuturaPorSala(Sala sala);
    List<String> obtenerAsientosOcupados(Long funcionId);
}
