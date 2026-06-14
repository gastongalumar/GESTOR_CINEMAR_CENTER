package GESTOR_CINEMAR_CENTER.DEV.controller;


import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.ActualizarResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.CustomizacionService;
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
    public ResponseEntity<ActualizarResponse> subirLogo(@RequestParam("logo") MultipartFile file) throws IOException {
        String rutaRel = customizacionService.guardarImagen(file, "logos");
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path(rutaRel).toUriString();
        return ResponseEntity.ok(new ActualizarResponse(rutaRel, url));
    }

    @PostMapping("/fondo")
    public ResponseEntity<ActualizarResponse> subirFondo(@RequestParam("fondo") MultipartFile file) throws IOException {
        String rutaRel = customizacionService.guardarImagen(file, "fondos");
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path(rutaRel).toUriString();
        return ResponseEntity.ok(new ActualizarResponse(rutaRel, url));
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
