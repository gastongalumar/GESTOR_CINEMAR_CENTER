package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.service.ReservaService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Webhooks de Mercado Pago")
public class PaymentWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookController.class);

    private final ReservaService reservaService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody(required = false) Map<String, Object> body,
                                          @RequestParam(required = false) Map<String, String> params) {
        try {
            String type = null;
            if (body != null && body.get("type") != null) {
                type = String.valueOf(body.get("type"));
            } else if (params != null && params.get("type") != null) {
                type = params.get("type");
            }

            if ("payment".equals(type)) {
                Object dataObj = body != null ? body.get("data") : null;
                String paymentId = null;
                if (dataObj instanceof Map) {
                    Object idObj = ((Map<?, ?>) dataObj).get("id");
                    if (idObj != null) {
                        paymentId = String.valueOf(idObj);
                    }
                }

                if (paymentId == null && params != null) {
                    if (params.get("data.id") != null) {
                        paymentId = params.get("data.id");
                    } else if (params.get("id") != null) {
                        paymentId = params.get("id");
                    }
                }

                if (paymentId != null && !paymentId.isBlank()) {
                    try {
                        PaymentClient client = new PaymentClient();
                        Payment payment = client.get(Long.parseLong(paymentId));

                        if ("approved".equals(payment.getStatus())) {
                            String externalReference = payment.getExternalReference();
                            if (externalReference != null && !externalReference.isBlank()) {
                                try {
                                    Long reservaId = Long.parseLong(externalReference);
                                    reservaService.marcarComoPagada(reservaId);
                                    log.info("Reserva {} marcada como pagada por webhook de Mercado Pago", reservaId);
                                } catch (NumberFormatException ex) {
                                    log.warn("external_reference no es un ID de reserva válido: {}", externalReference);
                                } catch (Exception ex) {
                                    log.warn("No se pudo marcar la reserva como pagada desde webhook", ex);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.warn("Error al consultar pago en Mercado Pago desde webhook", ex);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Error procesando webhook de Mercado Pago", ex);
        }

        return ResponseEntity.ok("OK");
    }
}
