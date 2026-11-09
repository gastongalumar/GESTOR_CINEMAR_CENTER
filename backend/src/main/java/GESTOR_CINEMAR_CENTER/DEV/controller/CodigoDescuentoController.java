package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.CodigoDescuentoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.descuento.ValidarCodigoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.CodigoDescuentoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.descuento.ValidarCodigoResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.CodigoDescuentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/codigos-descuento")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Códigos de descuento")
public class CodigoDescuentoController {

    private final CodigoDescuentoService service;

    @Operation(summary = "Validar código contra un subtotal (cliente)")
    @PostMapping("/validar")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<ValidarCodigoResponseDTO> validar(@Valid @RequestBody ValidarCodigoRequestDTO request) {
        return ResponseEntity.ok(service.validar(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<List<CodigoDescuentoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CodigoDescuentoResponseDTO> obtener(@Positive @PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CodigoDescuentoResponseDTO> crear(@Valid @RequestBody CodigoDescuentoRequestDTO request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CodigoDescuentoResponseDTO> actualizar(
            @Positive @PathVariable Long id,
            @Valid @RequestBody CodigoDescuentoRequestDTO request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<MensajeResponse> eliminar(@Positive @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Código eliminado"));
    }
}
