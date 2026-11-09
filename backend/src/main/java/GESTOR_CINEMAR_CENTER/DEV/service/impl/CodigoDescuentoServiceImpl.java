package GESTOR_CINEMAR_CENTER.DEV.service.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.CodigoDescuentoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.ValidarCodigoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.CodigoDescuentoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.ValidarCodigoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.TipoDescuento;
import GESTOR_CINEMAR_CENTER.DEV.exception.ConflictoRecursoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.RecursoNoEncontradoException;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.model.CodigoDescuento;
import GESTOR_CINEMAR_CENTER.DEV.repository.CodigoDescuentoRepository;
import GESTOR_CINEMAR_CENTER.DEV.service.CodigoDescuentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CodigoDescuentoServiceImpl implements CodigoDescuentoService {

    private final CodigoDescuentoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<CodigoDescuentoResponseDTO> listarTodos() {
        return repository.findAllByOrderByCodigoAsc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CodigoDescuentoResponseDTO obtener(Long id) {
        return toDto(find(id));
    }

    @Override
    @Transactional
    public CodigoDescuentoResponseDTO crear(CodigoDescuentoRequestDTO request) {
        String codigo = normalizarCodigo(request.getCodigo());
        if (repository.existsByCodigoIgnoreCase(codigo)) {
            throw new ConflictoRecursoException("Ya existe un código con ese valor");
        }
        validarValor(request);
        CodigoDescuento entity = new CodigoDescuento();
        aplicar(entity, request, codigo);
        entity.setUsosActuales(0);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public CodigoDescuentoResponseDTO actualizar(Long id, CodigoDescuentoRequestDTO request) {
        CodigoDescuento entity = find(id);
        String codigo = normalizarCodigo(request.getCodigo());
        repository.findByCodigoIgnoreCase(codigo).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new ConflictoRecursoException("Ya existe un código con ese valor");
            }
        });
        validarValor(request);
        aplicar(entity, request, codigo);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.delete(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ValidarCodigoResponseDTO validar(ValidarCodigoRequestDTO request) {
        String codigoNorm = normalizarCodigo(request.getCodigo());
        double subtotal = request.getSubtotal() == null ? 0 : request.getSubtotal();

        return repository.findByCodigoIgnoreCase(codigoNorm)
                .map(c -> {
                    String error = motivoInvalido(c);
                    if (error != null) {
                        return invalido(error);
                    }
                    double descuento = calcularDescuento(c, subtotal);
                    return new ValidarCodigoResponseDTO(
                            true,
                            "Código aplicado correctamente",
                            c.getCodigo(),
                            c.getTipo(),
                            c.getValor(),
                            descuento,
                            Math.max(0, subtotal - descuento));
                })
                .orElseGet(() -> invalido("Código de descuento no encontrado"));
    }

    @Override
    @Transactional
    public CodigoDescuento consumirSiValido(String codigo, double subtotal) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        CodigoDescuento entity = repository.findByCodigoIgnoreCase(normalizarCodigo(codigo))
                .orElseThrow(() -> new ReglaNegocioException("Código de descuento no encontrado"));
        String error = motivoInvalido(entity);
        if (error != null) {
            throw new ReglaNegocioException(error);
        }
        entity.setUsosActuales(entity.getUsosActuales() + 1);
        return repository.save(entity);
    }

    @Override
    public double calcularDescuento(CodigoDescuento codigo, double subtotal) {
        if (codigo == null || subtotal <= 0) {
            return 0;
        }
        double descuento;
        if (codigo.getTipo() == TipoDescuento.PORCENTAJE) {
            descuento = subtotal * (codigo.getValor() / 100.0);
        } else {
            descuento = codigo.getValor();
        }
        return Math.min(descuento, subtotal);
    }

    private String motivoInvalido(CodigoDescuento c) {
        if (!Boolean.TRUE.equals(c.getActivo())) {
            return "El código no está activo";
        }
        LocalDateTime ahora = LocalDateTime.now();
        if (c.getVigenteDesde() != null && ahora.isBefore(c.getVigenteDesde())) {
            return "El código aún no está vigente";
        }
        if (c.getVigenteHasta() != null && ahora.isAfter(c.getVigenteHasta())) {
            return "El código expiró";
        }
        if (c.getUsosMaximos() != null && c.getUsosActuales() >= c.getUsosMaximos()) {
            return "El código agotó sus usos";
        }
        return null;
    }

    private void validarValor(CodigoDescuentoRequestDTO request) {
        if (request.getTipo() == TipoDescuento.PORCENTAJE && request.getValor() > 100) {
            throw new ReglaNegocioException("El porcentaje no puede superar 100");
        }
    }

    private void aplicar(CodigoDescuento entity, CodigoDescuentoRequestDTO request, String codigo) {
        entity.setCodigo(codigo);
        entity.setTipo(request.getTipo());
        entity.setValor(request.getValor());
        entity.setActivo(request.getActivo() == null || request.getActivo());
        entity.setUsosMaximos(request.getUsosMaximos());
        entity.setVigenteDesde(request.getVigenteDesde());
        entity.setVigenteHasta(request.getVigenteHasta());
        entity.setDescripcion(request.getDescripcion() == null ? null : request.getDescripcion().trim());
    }

    private CodigoDescuento find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Código de descuento", id));
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private ValidarCodigoResponseDTO invalido(String mensaje) {
        return new ValidarCodigoResponseDTO(false, mensaje, null, null, null, 0.0, null);
    }

    private CodigoDescuentoResponseDTO toDto(CodigoDescuento c) {
        return new CodigoDescuentoResponseDTO(
                c.getId(),
                c.getCodigo(),
                c.getTipo(),
                c.getValor(),
                c.getActivo(),
                c.getUsosMaximos(),
                c.getUsosActuales(),
                c.getVigenteDesde(),
                c.getVigenteHasta(),
                c.getDescripcion());
    }
}
