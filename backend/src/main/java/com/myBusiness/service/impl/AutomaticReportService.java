package com.myBusiness.service.impl;

import com.myBusiness.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

import java.io.IOException;

@Service
public class AutomaticReportService {

    @Autowired
    private ReporteService reporteService; // Servicio de generación de reportes

    @Autowired
    private JavaMailSender mailSender;

    private static final String REPORT_RECIPIENT = "correo_responsable@example.com"; // Cambia esto al correo real

    // Método programado para enviar reportes mensuales automáticamente
    @Scheduled(cron = "0 0 9 1 * ?") // Ejecuta el primer día de cada mes a las 9:00 AM
    public void sendMonthlyReport() {
        try {
            // Generar el reporte de inventarios bajos
            Resource report = reporteService.generarReporte("INVENTARIOS_BAJOS", "EXCEL");

            // Enviar el reporte por correo
            sendReportByEmail(report, "Reporte de Inventarios Bajos", "Adjunto encontrarás el reporte de inventarios bajos.");
        } catch (Exception e) {
            e.printStackTrace(); // Manejo básico de errores
        }
    }

    // Método para enviar correos con adjuntos
    private void sendReportByEmail(Resource report, String subject, String text) throws MessagingException, IOException {
        // Crear un mensaje de correo con adjuntos
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Configurar los detalles del correo
        helper.setTo(REPORT_RECIPIENT);
        helper.setSubject(subject);
        helper.setText(text);
        helper.setFrom("tu_correo@gmail.com");

        // Adjuntar el archivo generado
        helper.addAttachment("low_stock_report.xlsx", new ByteArrayResource(report.getInputStream().readAllBytes()));

        // Enviar el correo
        mailSender.send(message);
    }
}
