package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.hold.CrearHoldRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.hold.LiberarHoldRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.hold.HoldResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.HoldService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@Validated
@RequestMapping("/api/holds")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class HoldController {

    private final HoldService holdService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<HoldResponseDTO> crear(
            @Valid @RequestBody CrearHoldRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(holdService.crearHold(
                request.getFuncionId(),
                request.getEtiqueta(),
                authentication.getName()
        ));
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<MensajeResponse> liberar(
            @Valid @RequestBody LiberarHoldRequestDTO request,
            Authentication authentication
    ) {
        holdService.liberarHold(request.getFuncionId(), request.getEtiqueta(), authentication.getName());
        return ResponseEntity.ok(new MensajeResponse("Hold liberado correctamente"));
    }

    @DeleteMapping("/funcion/{funcionId}")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<MensajeResponse> liberarTodos(
            @Positive @PathVariable Long funcionId,
            Authentication authentication
    ) {
        holdService.liberarTodosDelUsuario(funcionId, authentication.getName());
        return ResponseEntity.ok(new MensajeResponse("Holds liberados correctamente"));
    }
}
