package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;

import java.util.List;

public interface AsientoService {
    List<Asiento> obtenerAsientosPorEtiquetas(Sala sala, List<String> etiquetas);
    List<Asiento> obtenerPorSala(Sala sala);
    Asiento obtenerPorId(Long id);
    boolean existenAsientosEnSala(Sala sala);
    void eliminarPorSala(Sala sala);
    void guardarTodos(List<Asiento> asientos);
}