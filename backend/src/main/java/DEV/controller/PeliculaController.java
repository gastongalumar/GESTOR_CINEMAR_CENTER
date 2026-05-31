package DEV.controller;

import DEV.ActualizarResponse;
import DEV.MensajeResponse;
import DEV.dto.request.pelicula.ActualizarPeliculaRequestDTO;
import DEV.dto.request.pelicula.CrearPeliculaRequestDTO;
import DEV.dto.response.pelicula.PeliculaPageResponse;
import DEV.dto.response.pelicula.PeliculaResponseDTO;
import DEV.service.PeliculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/peliculas")
@CrossOrigin(origins = "http://localhost:4200")
public class PeliculaController {

    private final PeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<PeliculaResponseDTO>> listarPeliculas() {
        return ResponseEntity.ok(peliculaService.listarPeliculas());
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<PeliculaResponseDTO>> listarPeliculasVigentes() {
        return ResponseEntity.ok(peliculaService.listarPeliculasVigentes());
    }

    @GetMapping("/vigentes/paged")
    public ResponseEntity<PeliculaPageResponse> listarVigentesPaginado(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(peliculaService.listarVigentesPaginado(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(peliculaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PeliculaResponseDTO> crear(@Valid @RequestBody CrearPeliculaRequestDTO request) {
        return ResponseEntity.ok(peliculaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<PeliculaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarPeliculaRequestDTO request) {
        return ResponseEntity.ok(peliculaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponse("Película eliminada correctamente"));
    }

    @PostMapping("/subir-imagen")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<ActualizarResponse> subirImagen(@RequestParam("file") MultipartFile file){
        String rutaRel = peliculaService.guardarImagen(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path(rutaRel).toUriString();
        return ResponseEntity.ok(new ActualizarResponse(rutaRel, url));
    }

    @DeleteMapping("/{id}/imagen")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<MensajeResponse> eliminarImagen(@PathVariable Long id) {
        peliculaService.eliminarImagenDePelicula(id);
        return ResponseEntity.ok(new MensajeResponse("Imagen eliminada"));
    }
}
