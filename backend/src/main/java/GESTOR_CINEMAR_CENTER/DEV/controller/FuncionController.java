package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/funciones")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FuncionController {

    private final FuncionService funcionService;

    @GetMapping
    public ResponseEntity<List<FuncionResponseDTO>> listarFunciones() {
        return ResponseEntity.ok(funcionService.listarTodas());
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<FuncionResponseDTO>> listarVigentes() {
        return ResponseEntity.ok(funcionService.listarVigentes());
    }

    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<List<FuncionResponseDTO>> listarPorPelicula(@PathVariable Long peliculaId) {
        return ResponseEntity.ok(funcionService.listarPorPelicula(peliculaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(funcionService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FuncionResponseDTO> crear(@Valid @RequestBody CrearFuncionRequestDTO request) {
        return ResponseEntity.ok(funcionService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Función eliminada correctamente"));
    }
}

