package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Sala;
import GESTOR_CINEMAR_CENTER.DEV.repository.AsientoRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.AsientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("asientoService")
@RequiredArgsConstructor
public class AsientoServiceImpl implements AsientoService {

    private final AsientoRepository asientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Asiento> obtenerAsientosPorEtiquetas(Sala sala, List<String> etiquetas) {
        return etiquetas.stream()
                .map(etiqueta -> asientoRepository.findBySalaAndEtiqueta(sala, etiqueta)
                        .orElseThrow(() -> new ReglaNegocioException("Asiento no válido: " + etiqueta)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asiento> obtenerPorSala(Sala sala) {
        return asientoRepository.findBySala(sala);
    }

    @Override
    @Transactional(readOnly = true)
    public Asiento obtenerPorId(Long id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asiento", id));
    }

    @Override
    public boolean existenAsientosEnSala(Sala sala) {
        return asientoRepository.existsBySala(sala);
    }

    @Override
    @Transactional
    public void eliminarPorSala(Sala sala) {
        asientoRepository.deleteBySala(sala);
        asientoRepository.flush();
    }
    @Override
    @Transactional
    public void guardarTodos(List<Asiento> asientos) {
        asientoRepository.saveAll(asientos);
    }
}
