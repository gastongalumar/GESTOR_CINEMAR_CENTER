package DEV.controller;

import DEV.dto.request.ActualizarSalaRequestDTO;
import DEV.dto.request.CrearSalaRequestDTO;
import DEV.dto.response.AsientoResponseDTO;
import DEV.dto.response.SalaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MessageResponse;
import DEV.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "http://localhost:4200")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @GetMapping("/{id}/asientos")
    public ResponseEntity<List<AsientoResponseDTO>> listarAsientos(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.listarAsientosPorSala(id));
    }

    @PostMapping
    public ResponseEntity<SalaResponseDTO> crear(@Valid @RequestBody CrearSalaRequestDTO request) {
        return ResponseEntity.ok(salaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarSalaRequestDTO request) {
        return ResponseEntity.ok(salaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Long id) {
        salaService.eliminar(id);
        return ResponseEntity.ok(new MessageResponse("Sala eliminada correctamente"));
    }
}
