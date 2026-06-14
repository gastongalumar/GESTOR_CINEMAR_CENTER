package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.funcion.CrearFuncionesPorRangoRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.funcion.FuncionResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.service.FuncionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequestMapping("/api/funciones")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Tag(name = "Funciones", description = "Operaciones de consulta y gestión de funciones de proyección")
public class FuncionController {

    private final FuncionService funcionService;

    @Operation(summary = "Listar todas las funciones", description = "Retorna todas las funciones registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncionResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<FuncionResponseDTO>> listarFunciones() {
        return ResponseEntity.ok(funcionService.listarTodas());
    }

    @Operation(summary = "Listar funciones vigentes", description = "Retorna solo las funciones con fecha futura o en curso")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncionResponseDTO.class))))
    @GetMapping("/vigentes")
    public ResponseEntity<List<FuncionResponseDTO>> listarVigentes() {
        return ResponseEntity.ok(funcionService.listarVigentes());
    }

    @Operation(summary = "Listar funciones por película", description = "Retorna las funciones asociadas a una película específica")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncionResponseDTO.class))))
    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<List<FuncionResponseDTO>> listarPorPelicula(
            @Parameter(description = "ID de la película", required = true) @Positive @PathVariable Long peliculaId) {
        return ResponseEntity.ok(funcionService.listarPorPelicula(peliculaId));
    }

    @Operation(summary = "Obtener función por ID", description = "Busca una función específica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Función encontrada",
                    content = @Content(schema = @Schema(implementation = FuncionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Función no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FuncionResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la función", required = true) @Positive @PathVariable Long id) {
        return ResponseEntity.ok(funcionService.buscarPorId(id));
    }

    @Operation(summary = "Obtener asientos ocupados", description = "Retorna los asientos ya reservados para una función")
    @ApiResponse(responseCode = "200", description = "Asientos ocupados obtenidos correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @GetMapping("/{funcionId}/ocupados")
    public ResponseEntity<List<String>> obtenerAsientosOcupados(
            @Parameter(description = "ID de la función", required = true) @Positive @PathVariable Long funcionId) {
        return ResponseEntity.ok(funcionService.obtenerAsientosOcupados(funcionId));
    }

    @Operation(
            summary = "Crear una nueva función",
            description = "Programa una nueva función de proyección en una sala. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CrearFuncionRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Función creada correctamente",
                    content = @Content(schema = @Schema(implementation = FuncionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<FuncionResponseDTO> crear(@Valid @RequestBody CrearFuncionRequestDTO request) {
        return ResponseEntity.ok(funcionService.crear(request));
    }

    @Operation(
            summary = "Crear funciones por rango de fechas",
            description = "Genera funciones consecutivas en una sala para un rango de fechas, según la duración de la película. "
                    + "Si alguna función solapa con funciones existentes o el rango queda fuera de la cartelera, "
                    + "no se crea ninguna función. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CrearFuncionesPorRangoRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funciones creadas correctamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FuncionResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio incumplida"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping("/por-rango")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<FuncionResponseDTO>> crearFuncionesPorRango(
            @Valid @RequestBody CrearFuncionesPorRangoRequestDTO request) {
        return ResponseEntity.ok(funcionService.crearFuncionesPorRango(request));
    }

    @Operation(summary = "Eliminar una función", description = "Elimina una función de proyección del sistema. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Función eliminada correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Función no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminar(
            @Parameter(description = "ID de la función", required = true) @Positive @PathVariable Long id) {
        funcionService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Función eliminada correctamente"));
    }
}
