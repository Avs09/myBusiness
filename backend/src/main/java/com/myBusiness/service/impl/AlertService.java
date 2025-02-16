package com.myBusiness.service.impl;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AlertService {

    private final JavaMailSender mailSender;

    public AlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo con el reporte de productos con inventario bajo.
     *
     * @param reportData Los datos del reporte (archivo en bytes).
     */
    public void sendLowStockAlert(byte[] reportData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("admin@mybusiness.com"); // Cambiar por el correo del destinatario
            helper.setSubject("Alerta: Productos con inventario bajo");
            helper.setText("Se adjunta el reporte de productos con inventario bajo.");

            // Adjuntar el reporte
            ByteArrayResource resource = new ByteArrayResource(reportData);
            helper.addAttachment("low_stock_report.xlsx", resource);

            mailSender.send(message);

            System.out.println("Correo de alerta enviado exitosamente.");
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo de alerta: " + e.getMessage());
        }
    }
}
