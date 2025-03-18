package com.myBusiness.scheduler;

import com.myBusiness.service.ReporteService;
import com.myBusiness.service.impl.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * AlertScheduler is a scheduled job component that runs daily.
 * It generates a low stock report and sends alerts via email if inventory is low.
 */
@Component
public class AlertScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AlertScheduler.class);

    // Service to generate reports (e.g., low stock report)
    private final ReporteService reporteService;
    // Service to handle sending email alerts
    private final AlertService alertService;

    // Externalized cron expression for scheduling the alert job (default: 8:00 AM every day)
    private final String alertCron;

    /**
     * Constructor for dependency injection.
     *
     * @param reporteService the service used to generate reports.
     * @param alertService   the service used to send alert emails.
     * @param alertCron      the cron expression to schedule the alert job.
     */
    public AlertScheduler(ReporteService reporteService, AlertService alertService,
                          @Value("${scheduler.alert.cron:0 0 8 * * ?}") String alertCron) {
        this.reporteService = reporteService;
        this.alertService = alertService;
        this.alertCron = alertCron;
    }

    /**
     * Scheduled job to execute daily at the configured time (default 8:00 AM).
     * It generates a low stock report and sends email alerts.
     */
    @Scheduled(cron = "${scheduler.alert.cron:0 0 8 * * ?}")
    public void sendLowStockAlerts() {
        try {
            // Generate the low stock report as a byte array.
            byte[] reportData = reporteService.generateLowStockReportAsByteArray();
            // Send an alert email with the report attached.
            alertService.sendLowStockAlert(reportData);
            logger.info("Low stock alert sent successfully.");
        } catch (IOException e) {
            logger.error("Error generating or sending low stock report: {}", e.getMessage(), e);
        }
    }
}
