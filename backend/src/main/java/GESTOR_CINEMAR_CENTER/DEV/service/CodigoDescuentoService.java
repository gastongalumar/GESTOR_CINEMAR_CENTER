package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.CodigoDescuentoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.ValidarCodigoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.CodigoDescuentoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.ValidarCodigoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.CodigoDescuento;

import java.util.List;

public interface CodigoDescuentoService {
    List<CodigoDescuentoResponseDTO> listarTodos();
    CodigoDescuentoResponseDTO obtener(Long id);
    CodigoDescuentoResponseDTO crear(CodigoDescuentoRequestDTO request);
    CodigoDescuentoResponseDTO actualizar(Long id, CodigoDescuentoRequestDTO request);
    void eliminar(Long id);
    ValidarCodigoResponseDTO validar(ValidarCodigoRequestDTO request);
    /** Aplica y consume un uso; lanza si no es válido. */
    CodigoDescuento consumirSiValido(String codigo, double subtotal);
    double calcularDescuento(CodigoDescuento codigo, double subtotal);
}
