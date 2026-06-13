package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.customizacion.CustomizacionRequest;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.customizacion.CustomizacionResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.service.CustomizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customizacion")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Customización", description = "Operaciones de personalización del tema de la aplicación")
public class CustomizacionController {

    private final CustomizacionService customizacionService;

    @Operation(summary = "Obtener configuración actual", description = "Retorna la configuración de tema personalizado del cine")
    @ApiResponse(responseCode = "200", description = "Configuración obtenida correctamente",
            content = @Content(schema = @Schema(implementation = CustomizacionResponse.class)))
    @GetMapping
    public ResponseEntity<CustomizacionResponse> obtener() {
        return ResponseEntity.ok(customizacionService.obtener());
    }

    @Operation(
            summary = "Guardar configuración",
            description = "Actualiza la configuración de tema personalizado. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CustomizacionRequest.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración guardada correctamente",
                    content = @Content(schema = @Schema(implementation = CustomizacionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CustomizacionResponse> guardar(@Valid @RequestBody CustomizacionRequest request) {
        return ResponseEntity.ok(customizacionService.guardar(request));
    }

    @Operation(summary = "Subir logo", description = "Sube un archivo de logo y retorna la URL pública. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Logo subido correctamente",
            content = @Content(schema = @Schema(implementation = CustomizacionResponse.class)))
    @PostMapping("/logo")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CustomizacionResponse> subirLogo(
            @Parameter(description = "Archivo de logo", required = true) @RequestParam("logo") MultipartFile file) throws java.io.IOException {
        customizacionService.guardarImagen(file, "logos");
        CustomizacionResponse response = customizacionService.obtener();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Subir fondo", description = "Sube un archivo de fondo y retorna la URL pública. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Fondo subido correctamente",
            content = @Content(schema = @Schema(implementation = CustomizacionResponse.class)))
    @PostMapping("/fondo")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<CustomizacionResponse> subirFondo(
            @Parameter(description = "Archivo de fondo", required = true) @RequestParam("fondo") MultipartFile file) throws java.io.IOException {
        customizacionService.guardarImagen(file, "fondos");
        CustomizacionResponse response = customizacionService.obtener();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar logo", description = "Elimina el logo personalizado. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Logo eliminado correctamente",
            content = @Content(schema = @Schema(implementation = MensajeResponse.class)))
    @DeleteMapping("/logo")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminarLogo() {
        customizacionService.eliminarImagen("logos");
        return ResponseEntity.ok(new MensajeResponse("Logo eliminado correctamente"));
    }

    @Operation(summary = "Eliminar fondo", description = "Elimina el fondo personalizado. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Fondo eliminado correctamente",
            content = @Content(schema = @Schema(implementation = MensajeResponse.class)))
    @DeleteMapping("/fondo")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminarFondo() {
        customizacionService.eliminarImagen("fondos");
        return ResponseEntity.ok(new MensajeResponse("Fondo eliminado correctamente"));
    }
}

