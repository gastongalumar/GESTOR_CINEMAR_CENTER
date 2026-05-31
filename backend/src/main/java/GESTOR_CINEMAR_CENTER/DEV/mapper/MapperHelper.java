package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.enums.MetodoPago;
import GESTOR_CINEMAR_CENTER.DEV.model.Asiento;
import GESTOR_CINEMAR_CENTER.DEV.model.Usuario;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperHelper {


    @Named("nombreCompleto")
    public static String nombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return usuario.getNombre() + " " + usuario.getApellido();
    }

    @Named("enumName")
    public static String enumName(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    @Named("metodoPagoNormalizado")
    public static String metodoPagoNormalizado(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            return null;
        }
        if (!MetodoPago.esValido(metodoPago)) {
            throw new IllegalArgumentException("Método de pago no permitido: " + metodoPago + "  ");
        }
        return MetodoPago.fromString(metodoPago).name();
    }

    @Named("asientosAEtiquetas")
    public static List<String> asientosAEtiquetas(List<Asiento> asientos) {
        if (asientos == null) {
            return null;
        }
        return asientos.stream()
                .map(Asiento::getEtiqueta)
                .collect(Collectors.toList());
    }
}
