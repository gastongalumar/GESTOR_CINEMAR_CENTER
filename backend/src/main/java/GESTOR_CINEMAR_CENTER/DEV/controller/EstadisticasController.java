package GESTOR_CINEMAR_CENTER.DEV.controller;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas.*;
import GESTOR_CINEMAR_CENTER.DEV.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    // Requiere rol ADMIN según SecurityConfig; se puede controlar también desde SecurityConfig
    @GetMapping("/dashboard/resumen")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<DashboardResumenDTO> obtenerResumen() {
        return ResponseEntity.ok(estadisticasService.obtenerResumen());
    }

    @GetMapping("/reservas/por-fecha")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<VentaDiariaDTO>> ventasPorFecha(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(estadisticasService.ventasDiarias(dias));
    }

    @GetMapping("/ocupacion/pelicula")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<VentaPeliculaDTO>> ventasPorPelicula() {
        return ResponseEntity.ok(estadisticasService.ventasPorPelicula());
    }

    @GetMapping("/ocupacion/sala")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<VentaSalaDTO>> ventasPorSala() {
        return ResponseEntity.ok(estadisticasService.ventasPorSala());
    }

    @GetMapping("/ocupacion/funcion")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<OcupacionFuncionDTO>> ocupacionFunciones(@RequestParam(defaultValue = "false") boolean soloVigentes) {
        return ResponseEntity.ok(estadisticasService.ocupacionFunciones(soloVigentes));
    }

    @GetMapping("/ingresos/metodos-pago")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<MetodoPagoDTO>> metodosPago() {
        return ResponseEntity.ok(estadisticasService.metodosPago());
    }
}