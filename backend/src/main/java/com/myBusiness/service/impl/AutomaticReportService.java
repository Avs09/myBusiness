package com.myBusiness.service.impl;

import com.myBusiness.service.ReporteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * AutomaticReportService schedules and sends monthly reports via email.
 * This service leverages ReporteService for report generation and JavaMailSender for email delivery.
 */
@Service
public class AutomaticReportService {

    private static final Logger logger = LoggerFactory.getLogger(AutomaticReportService.class);

    private final ReporteService reporteService;
    private final JavaMailSender mailSender;
    
    private static final String REPORT_RECIPIENT = "correo_responsable@example.com"; // Update with actual recipient email

    public AutomaticReportService(ReporteService reporteService, JavaMailSender mailSender) {
        this.reporteService = reporteService;
        this.mailSender = mailSender;
    }

    /**
     * Scheduled method that sends the monthly low stock report.
     * Executes on the first day of every month at 9:00 AM.
     */
    @Scheduled(cron = "0 0 9 1 * ?") // Runs at 9:00 AM on the 1st day of each month
    public void sendMonthlyReport() {
        try {
            // Generate the low stock report in EXCEL format
            Resource report = reporteService.generarReporte("INVENTARIOS_BAJOS", "EXCEL");
            // Send the generated report via email asynchronously
            sendReportByEmail(report, "Low Stock Report", "Attached is the low stock report.");
            logger.info("Monthly report sent successfully.");
        } catch (Exception e) {
            logger.error("Error sending monthly report: {}", e.getMessage(), e);
        }
    }

    /**
     * Helper method to send an email with an attached report asynchronously.
     *
     * @param report  The report to attach.
     * @param subject The email subject.
     * @param text    The email body text.
     * @throws MessagingException If an error occurs while creating the message.
     * @throws IOException        If an error occurs while reading the report data.
     */
    @Async("taskExecutor")
    private void sendReportByEmail(Resource report, String subject, String text) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(REPORT_RECIPIENT);
        helper.setSubject(subject);
        helper.setText(text);
        helper.setFrom("tu_correo@gmail.com");

        ByteArrayResource attachment = new ByteArrayResource(report.getInputStream().readAllBytes());
        helper.addAttachment("low_stock_report.xlsx", attachment);

        mailSender.send(message);
        logger.info("Report email sent to {}", REPORT_RECIPIENT);
    }
}
