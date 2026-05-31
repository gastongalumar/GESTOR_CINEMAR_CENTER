package GESTOR_CINEMAR_CENTER.DEV.mapper;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.ActualizarMetodoPagoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.reserva.CrearReservaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ReservaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.reserva.ValidacionTicketResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.model.Cliente;
import GESTOR_CINEMAR_CENTER.DEV.model.Funcion;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.AfterMapping;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;

@Mapper(componentModel = "spring", uses = MapperHelper.class)
public interface ReservaMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente", target = "clienteNombre", qualifiedByName = "nombreCompleto")
    @Mapping(source = "funcion.id", target = "funcionId")
    @Mapping(source = "funcion.pelicula.nombre", target = "peliculaNombre")
    @Mapping(source = "funcion.sala.nombre", target = "salaNombre")
    @Mapping(source = "funcion.horario", target = "horarioFuncion")
    @Mapping(source = "asientos", target = "asientosSeleccionados", qualifiedByName = "asientosAEtiquetas")
    @Mapping(source = "estadoReserva", target = "estadoReserva")
    ReservaResponseDTO toResponse(Reserva reserva);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroTicket", ignore = true)
    @Mapping(target = "codigoOR", ignore = true)
    @Mapping(target = "montoTotal", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    @Mapping(target = "fechaValidacion", ignore = true)
    @Mapping(target = "estadoReserva", ignore = true)
    @Mapping(target = "metodoPago", ignore = true)
    @Mapping(target = "asientos", ignore = true)
    @Mapping(source = "cliente", target = "cliente")
    @Mapping(source = "funcion", target = "funcion")
    Reserva toEntity(CrearReservaRequestDTO request, Cliente cliente, Funcion funcion);

    @Mapping(source = "numeroTicket", target = "numeroTicket")
    @Mapping(source = "montoTotal", target = "montoTotal")
    @Mapping(source = "asientos", target = "asientos", qualifiedByName = "asientosAEtiquetas")
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "mensaje", ignore = true)
    ValidacionTicketResponseDTO toValidacionTicket(Reserva reserva, @Context boolean marcarUsadoAhora);

    @AfterMapping
    default void configurarMensaje(Reserva reserva, @Context boolean marcarUsadoAhora, @MappingTarget ValidacionTicketResponseDTO target) {
        if (marcarUsadoAhora) {
            target.setEstado("VALIDADO_AHORA");
            target.setMensaje("El ticket ha sido validado y marcado como usado exitosamente.");
        } else if (reserva.getEstadoReserva() == EstadoReserva.VALIDADA) {
            target.setEstado("YA_VALIDADO");
            target.setMensaje("El ticket ya fue validado anteriormente.");
        } else if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            target.setEstado("CANCELADA");
            target.setMensaje("La reserva asociada a este ticket ha sido cancelada.");
        } else {
            target.setEstado("VALIDO");
            target.setMensaje("El ticket es válido.");
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "metodoPago", ignore = true)
    void actualizarMetodoPago(ActualizarMetodoPagoRequestDTO request, @MappingTarget Reserva reserva);
}