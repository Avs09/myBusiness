package com.myBusiness.service.impl;

import com.myBusiness.model.Product;
import com.myBusiness.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * InventoryAlertService periodically checks for products with low inventory
 * and sends an email alert listing the affected products.
 */
@Service
public class InventoryAlertService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryAlertService.class);

    private final ProductRepository productRepository;
    private final JavaMailSender mailSender;

    // Threshold value for low inventory
    private static final int THRESHOLD = 10;

    public InventoryAlertService(ProductRepository productRepository, JavaMailSender mailSender) {
        this.productRepository = productRepository;
        this.mailSender = mailSender;
    }

    /**
     * Scheduled method that runs daily at 9:00 AM.
     * It finds products with quantities below the threshold and triggers an email alert.
     */
    @Scheduled(cron = "0 0 9 * * ?")  // Executes every day at 9:00 AM
    public void checkLowStockAndSendAlerts() {
        try {
            List<Product> lowStockProducts = productRepository.findByQuantityLessThan(THRESHOLD);
            if (!lowStockProducts.isEmpty()) {
                logger.info("Found {} products with low stock.", lowStockProducts.size());
                sendLowStockAlert(lowStockProducts);
            } else {
                logger.info("No low stock products found.");
            }
        } catch (Exception e) {
            logger.error("Error checking low stock products", e);
        }
    }

    /**
     * Constructs and sends a simple email alert containing a list of low stock products asynchronously.
     *
     * @param lowStockProducts List of products with inventory below the threshold.
     */
    @Async("taskExecutor")
    private void sendLowStockAlert(List<Product> lowStockProducts) {
        try {
            StringBuilder messageBody = new StringBuilder("Low Stock Products:\n\n");
            lowStockProducts.forEach(product ->
                    messageBody.append("ID: ").append(product.getId())
                            .append(", Name: ").append(product.getName())
                            .append(", Quantity: ").append(product.getQuantity())
                            .append("\n")
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("correo_responsable@example.com");  // Hard-coded recipient email
            message.setSubject("Alert: Low Inventory");
            message.setText(messageBody.toString());
            message.setFrom("tu_correo@gmail.com");  // Hard-coded sender email

            mailSender.send(message);
            logger.info("Low stock alert email sent successfully.");
        } catch (Exception e) {
            logger.error("Error sending low stock alert email", e);
        }
    }
}
