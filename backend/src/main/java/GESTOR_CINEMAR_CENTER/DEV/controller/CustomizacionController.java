package GESTOR_CINEMAR_CENTER.DEV.controller;


import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.ActualizarResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.CustomizacionService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/api/customizacion")
@Hidden
@CrossOrigin(origins = "http://localhost:4200")
public class CustomizacionController {

    private final CustomizacionService customizacionService;

    public CustomizacionController(CustomizacionService customizacionService) {
        this.customizacionService = customizacionService;
    }

    @GetMapping
    public ResponseEntity<CustomizacionResponse> obtener() {
        return ResponseEntity.ok(customizacionService.obtener());
    }

    @PostMapping
    public ResponseEntity<CustomizacionResponse> guardar(@Valid @RequestBody CustomizacionRequest request) {
        return ResponseEntity.ok(customizacionService.guardar(request));
    }

    @PostMapping("/logo")
    public ResponseEntity<CustomizacionResponse> subirLogo(@RequestParam("logo") MultipartFile file) throws IOException {
        customizacionService.guardarImagen(file, "logos");
        return ResponseEntity.ok(customizacionService.obtener());
    }

    @PostMapping("/fondo")
    public ResponseEntity<CustomizacionResponse> subirFondo(@RequestParam("fondo") MultipartFile file) throws IOException {
        customizacionService.guardarImagen(file, "fondos");
        return ResponseEntity.ok(customizacionService.obtener());
    }

    @DeleteMapping("/logo")
    public ResponseEntity<MensajeResponse> eliminarLogo() {
        customizacionService.eliminarImagen("logos");
        return ResponseEntity.ok(new MensajeResponse("Logo eliminado"));
    }

    @DeleteMapping("/fondo")
    public ResponseEntity<MensajeResponse> eliminarFondo() {
        customizacionService.eliminarImagen("fondos");
        return ResponseEntity.ok(new MensajeResponse("Fondo eliminado"));
    }
}
