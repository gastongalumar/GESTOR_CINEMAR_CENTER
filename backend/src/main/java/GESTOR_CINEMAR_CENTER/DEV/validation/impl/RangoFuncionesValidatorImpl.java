package GESTOR_CINEMAR_CENTER.DEV.validation.impl;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionesPorRangoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.validation.interfaces.RangoFuncionesValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RangoFuncionesValidatorImpl implements ConstraintValidator<RangoFuncionesValido, CrearFuncionesPorRangoRequestDTO> {

    @Override
    public boolean isValid(CrearFuncionesPorRangoRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (dto.getFechaDesde() != null && dto.getFechaHasta() != null
                && dto.getFechaHasta().isBefore(dto.getFechaDesde())) {
            context.buildConstraintViolationWithTemplate("La fecha hasta debe ser igual o posterior a la fecha desde")
                    .addPropertyNode("fechaHasta")
                    .addConstraintViolation();
            return false;
        }

        if (dto.getHoraInicio() != null && dto.getHoraFin() != null
                && !dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            context.buildConstraintViolationWithTemplate("La hora de fin debe ser posterior a la hora de inicio")
                    .addPropertyNode("horaFin")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
