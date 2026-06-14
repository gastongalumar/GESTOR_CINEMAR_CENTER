package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.FechasPeliculaValidas;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasPeliculaValidatorImpl implements ConstraintValidator<FechasPeliculaValidas, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof CrearPeliculaRequestDTO dto) {
            return dto.getFechaEstreno() != null
                    && dto.getFechaSalida() != null
                    && dto.getFechaSalida().isAfter(dto.getFechaEstreno());
        }

        if (value instanceof ActualizarPeliculaRequestDTO dto) {
            if (dto.getFechaEstreno() == null || dto.getFechaSalida() == null) {
                return true;
            }
            return dto.getFechaSalida().isAfter(dto.getFechaEstreno());
        }

        return true;
    }
}
