package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.config.MercadoPagoConfiguration;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pago.CrearPreferenciaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pago.CrearPreferenciaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.enums.EstadoReserva;
import GESTOR_CINEMAR_CENTER.DEV.exception.ReglaNegocioException;
import GESTOR_CINEMAR_CENTER.DEV.model.Reserva;
import GESTOR_CINEMAR_CENTER.DEV.repository.ReservaRepository;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Integración con Mercado Pago Checkout Pro")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final ReservaRepository reservaRepository;
    private final MercadoPagoConfiguration mercadoPagoConfig;

    @Operation(summary = "Crear preferencia de pago", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/crear-preferencia")
    public ResponseEntity<CrearPreferenciaResponseDTO> crearPreferencia(
            Authentication authentication,
            @Valid @RequestBody CrearPreferenciaRequestDTO request) {

        String email = authentication.getName();
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new ReglaNegocioException("La reserva no existe"));

        if (reserva.getCliente() == null || reserva.getCliente().getEmail() == null
                || !reserva.getCliente().getEmail().equalsIgnoreCase(email)) {
            throw new ReglaNegocioException("No autorizado para pagar esta reserva");
        }

        if (Boolean.TRUE.equals(reserva.getPagada())) {
            throw new ReglaNegocioException("La reserva ya se encuentra pagada");
        }

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA
                || reserva.getEstadoReserva() == EstadoReserva.EXPIRADA) {
            throw new ReglaNegocioException("La reserva no es válida para pago");
        }

        if (reserva.getMontoTotal() == null || reserva.getMontoTotal() <= 0) {
            throw new ReglaNegocioException("El monto de la reserva es inválido");
        }

        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            int cantidad = reserva.getAsientos() == null ? 1 : reserva.getAsientos().size();
            if (cantidad < 1) {
                cantidad = 1;
            }

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(String.valueOf(reserva.getId()))
                    .title("Entradas CineMarCenter - Reserva " + reserva.getNumeroTicket())
                    .quantity(cantidad)
                    .currencyId("ARS")
                    .unitPrice(BigDecimal.valueOf(reserva.getMontoTotal() / (double) cantidad))
                    .build();
            items.add(item);

            String payerEmail = reserva.getCliente().getEmail();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(payerEmail)
                    .build();

            String frontendUrl = mercadoPagoConfig.getFrontendUrl();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(frontendUrl + "/pago/exito")
                    .failure(frontendUrl + "/pago/error")
                    .pending(frontendUrl + "/pago/pendiente")
                    .build();

            PreferenceRequest.PreferenceRequestBuilder prefBuilder = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .notificationUrl(mercadoPagoConfig.getNotificationUrl())
                    .externalReference(String.valueOf(reserva.getId()));

            // Mercado Pago sólo acepta auto_return cuando las back_urls son HTTPS.
            // En desarrollo local (http://localhost:4200) se omite.
            if (frontendUrl.startsWith("https://")) {
                prefBuilder.autoReturn("approved");
            }

            PreferenceRequest preferenceRequest = prefBuilder.build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            CrearPreferenciaResponseDTO response = new CrearPreferenciaResponseDTO();
            response.setPreferenceId(preference.getId());
            response.setSandboxInitPoint(preference.getSandboxInitPoint());
            // En modo prueba se usa el checkout de sandbox; en producción, el real.
            response.setInitPoint(mercadoPagoConfig.isTestMode()
                    ? preference.getSandboxInitPoint()
                    : preference.getInitPoint());

            if (log.isDebugEnabled()) {
                String token = mercadoPagoConfig.getAccessToken();
                log.debug("Preferencia creada para reserva {} con token prefijo {}", reserva.getId(),
                        token != null && token.length() > 10 ? token.substring(0, 10) : "***");
            }

            return ResponseEntity.ok(response);
        } catch (MPApiException ex) {
            log.error("Error MP API al crear preferencia: status={}, content={}", ex.getStatusCode(),
                    ex.getApiResponse() != null ? ex.getApiResponse().getContent() : null);
            throw new ReglaNegocioException("No se pudo crear la preferencia de pago");
        } catch (Exception ex) {
            log.error("Error al crear preferencia de pago", ex);
            throw new ReglaNegocioException("No se pudo crear la preferencia de pago");
        }
    }
}
