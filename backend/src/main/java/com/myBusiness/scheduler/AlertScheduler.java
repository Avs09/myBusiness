package com.myBusiness.scheduler;

import com.myBusiness.service.ReporteService;
import com.myBusiness.service.impl.AlertService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AlertScheduler {

    private final ReporteService reporteService;
    private final AlertService alertService;

    public AlertScheduler(ReporteService reporteService, AlertService alertService) {
        this.reporteService = reporteService;
        this.alertService = alertService;
    }

    /**
     * Job programado para ejecutar cada día a las 8:00 AM.
     * Detecta productos con inventario bajo y envía alertas por correo.
     */
    @Scheduled(cron = "0 0 8 * * ?") // 8:00 AM todos los días
    public void sendLowStockAlerts() {
        try {
            // Generar reporte de inventarios bajos
            byte[] reportData = reporteService.generateLowStockReportAsByteArray();
            
            // Enviar alerta
            alertService.sendLowStockAlert(reportData);
        } catch (IOException e) {
            System.err.println("Error al generar o enviar el reporte de inventarios bajos: " + e.getMessage());
        }
    }
}
