package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.ActualizarSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.sala.CrearSalaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.CapacidadSalaValida;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CapacidadSalaValidatorImpl implements ConstraintValidator<CapacidadSalaValida, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Integer filas = null;
        Integer columnas = null;

        if (value instanceof CrearSalaRequestDTO dto) {
            filas = dto.getFilas();
            columnas = dto.getColumnas();
        } else if (value instanceof ActualizarSalaRequestDTO dto) {
            filas = dto.getFilas();
            columnas = dto.getColumnas();
        }

        if (filas == null || columnas == null) {
            return true;
        }

        return (long) filas * columnas <= 320;
    }
}
