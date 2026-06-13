package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.mensaje.MensajeResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaPageResponse;
import GESTOR_CINEMAR_CENTER.DEV.dto.response.pelicula.PeliculaResponseDTO;
import GESTOR_CINEMAR_CENTER.DEV.service.PeliculaService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/peliculas")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Películas", description = "Operaciones de consulta y gestión del catálogo de películas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    @Operation(summary = "Listar todas las películas", description = "Retorna el catálogo completo de películas registradas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PeliculaResponseDTO.class))))
    @GetMapping
    public ResponseEntity<List<PeliculaResponseDTO>> listarPeliculas() {
        return ResponseEntity.ok(peliculaService.listarPeliculas());
    }

    @Operation(summary = "Listar películas vigentes", description = "Retorna solo las películas actualmente en cartelera")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PeliculaResponseDTO.class))))
    @GetMapping("/vigentes")
    public ResponseEntity<List<PeliculaResponseDTO>> listarPeliculasVigentes() {
        return ResponseEntity.ok(peliculaService.listarPeliculasVigentes());
    }

    @Operation(summary = "Listar películas vigentes paginadas", description = "Retorna películas en cartelera con paginación")
    @ApiResponse(responseCode = "200", description = "Página obtenida correctamente",
            content = @Content(schema = @Schema(implementation = PeliculaPageResponse.class)))
    @GetMapping("/vigentes/paged")
    public ResponseEntity<PeliculaPageResponse> listarVigentesPaginado(
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por página") @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(peliculaService.listarVigentesPaginado(page, size));
    }

    @Operation(summary = "Obtener película por ID", description = "Busca una película específica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película encontrada",
                    content = @Content(schema = @Schema(implementation = PeliculaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la película", required = true) @Positive(message = "El ID debe ser mayor a 0") @PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.buscarPorId(id));
    }

    @Operation(
            summary = "Crear una nueva película",
            description = "Registra una película en el catálogo. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CrearPeliculaRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película creada correctamente",
                    content = @Content(schema = @Schema(implementation = PeliculaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PeliculaResponseDTO> crear(@Valid @RequestBody CrearPeliculaRequestDTO request) {
        return ResponseEntity.ok(peliculaService.crear(request));
    }

    @Operation(
            summary = "Actualizar una película",
            description = "Modifica los datos de una película existente. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ActualizarPeliculaRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = PeliculaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PeliculaResponseDTO> actualizar(
            @Parameter(description = "ID de la película", required = true) @PathVariable Long id,
            @Valid @RequestBody ActualizarPeliculaRequestDTO request) {
        return ResponseEntity.ok(peliculaService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una película", description = "Elimina una película del catálogo. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película eliminada correctamente",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Película no encontrada"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminar(
            @Parameter(description = "ID de la película", required = true) @PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Película eliminada correctamente"));
    }

    @Operation(summary = "Subir imagen de película", description = "Guarda una imagen para una película. Requiere rol ADMINISTRADOR",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> subirImagen(
            @Parameter(description = "ID de la película", required = true) @PathVariable Long id,
            @Parameter(description = "Archivo de imagen (JPEG, PNG, WEBP. Máx 5MB)") @RequestParam("file") MultipartFile file) {
        String ruta = peliculaService.guardarImagen(id, file);
        return ResponseEntity.ok(new MensajeResponse("Imagen guardada correctamente: " + ruta));
    }

    @Operation(summary = "Filtrar películas por nombre", description = "Busca películas cuyo nombre contiene el texto especificado")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PeliculaResponseDTO.class))))
    @GetMapping("/filtro/nombre")
    public ResponseEntity<List<PeliculaResponseDTO>> filtrarPorNombre(
            @Parameter(description = "Nombre o parte del nombre de la película", required = true) @RequestParam String nombre) {
        return ResponseEntity.ok(peliculaService.filtrarVigentesPorNombre(nombre));
    }

    @Operation(summary = "Filtrar películas por género", description = "Busca películas cuyo género contiene el texto especificado")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PeliculaResponseDTO.class))))
    @GetMapping("/filtro/genero")
    public ResponseEntity<List<PeliculaResponseDTO>> filtrarPorGenero(
            @Parameter(description = "Género de la película", required = true) @RequestParam String genero) {
        return ResponseEntity.ok(peliculaService.filtrarVigentesPorGenero(genero));
    }

    @Operation(summary = "Obtener película por función", description = "Retorna la película asociada a una función específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Película encontrada",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PeliculaResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Función no encontrada")
    })
    @GetMapping("/filtro/funcion/{funcionId}")
    public ResponseEntity<List<PeliculaResponseDTO>> filtrarPorFuncion(
            @Parameter(description = "ID de la función", required = true) @PathVariable Long funcionId) {
        return ResponseEntity.ok(peliculaService.filtrarPorFuncion(funcionId));
    }
}
