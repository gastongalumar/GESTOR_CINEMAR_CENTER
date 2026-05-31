package DEV.service;


import DEV.exception.RecursoNoEncontradoException;
import DEV.exception.ReglaNegocioException;
import DEV.model.Asiento;
import DEV.model.Sala;
import DEV.repository.AsientoRepository;

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