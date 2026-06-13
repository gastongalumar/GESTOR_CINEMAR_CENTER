package GESTOR_CINEMAR_CENTER.DEV.service;

import GESTOR_CINEMAR_CENTER.DEV.dto.response.estadisticas.*;
import java.util.List;

public interface EstadisticasService {
    DashboardResumenDTO obtenerResumen();
    List<VentaDiariaDTO> ventasDiarias(int dias);
    List<VentaPeliculaDTO> ventasPorPelicula();
    List<VentaSalaDTO> ventasPorSala();
    List<OcupacionFuncionDTO> ocupacionFunciones(boolean soloVigentes);
    List<MetodoPagoDTO> metodosPago();
}