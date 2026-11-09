package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.candy.CandyComboRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.candy.CandyComboResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.CandyComboService;
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
@RequestMapping("/api/candy-combos")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Candy Combos", description = "Combos del Candy Bar")
public class CandyComboController {

    private final CandyComboService service;

    @Operation(summary = "Listar combos activos (checkout / candy público)")
    @GetMapping("/activos")
    public ResponseEntity<List<CandyComboResponseDTO>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    @Operation(summary = "Listar todos los combos (admin)")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<List<CandyComboResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CandyComboResponseDTO> obtener(@Positive @PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CandyComboResponseDTO> crear(@Valid @RequestBody CandyComboRequestDTO request) {
        return ResponseEntity.ok(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<CandyComboResponseDTO> actualizar(
            @Positive @PathVariable Long id,
            @Valid @RequestBody CandyComboRequestDTO request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'SUPERADMIN')")
    public ResponseEntity<MensajeResponse> eliminar(@Positive @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Combo eliminado"));
    }
}
