package GESTOR_CINEMAR_CENTER.DEV.service;


import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.AsientoRepository;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsientoService {

    private final AsientoRepository asientoRepository;

    @Transactional(readOnly = true)
    public List<Asiento> obtenerAsientosPorEtiquetas(Sala sala, List<String> etiquetas) {
        return etiquetas.stream()
                .map(etiqueta -> asientoRepository.findBySalaAndEtiqueta(sala, etiqueta)
                        .orElseThrow(() -> new ReglaNegocioException("Asiento no válido: " + etiqueta)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Asiento> obtenerPorSala(Sala sala) {
        return asientoRepository.findBySala(sala);
    }

    @Transactional(readOnly = true)
    public Asiento obtenerPorId(Long id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asiento", id));
    }

    public boolean existenAsientosEnSala(Sala sala) {
        return asientoRepository.existsBySala(sala);
    }

    @Transactional
    public void eliminarPorSala(Sala sala) {
        asientoRepository.deleteBySala(sala);
    }
}