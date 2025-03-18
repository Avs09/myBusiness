package com.myBusiness.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * AlertService is responsible for sending email alerts.
 * In this implementation, it sends an email containing the low stock report as an attachment.
 */
@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private final JavaMailSender mailSender;

    public AlertService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email alert with the low stock report attached asynchronously.
     *
     * @param reportData The report data as a byte array.
     */
    @Async("taskExecutor")
    public void sendLowStockAlert(byte[] reportData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo("admin@mybusiness.com"); // Recipient email hard-coded
            helper.setSubject("Alert: Low Stock Products");
            helper.setText("Please find attached the low stock report.");

            // Attach the report as a ByteArrayResource.
            ByteArrayResource resource = new ByteArrayResource(reportData);
            helper.addAttachment("low_stock_report.xlsx", resource);

            mailSender.send(message);
            logger.info("Low stock alert email sent successfully to admin@mybusiness.com");
        } catch (MessagingException e) {
            logger.error("Error sending low stock alert email: {}", e.getMessage(), e);
        }
    }
}
