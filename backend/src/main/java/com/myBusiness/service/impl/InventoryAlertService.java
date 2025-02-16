package com.myBusiness.service.impl;

import com.myBusiness.model.Product;
import com.myBusiness.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryAlertService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int THRESHOLD = 10; // Umbral de inventario bajo

    // Método que busca productos con inventario bajo y envía una alerta por correo
    @Scheduled(cron = "0 0 9 * * ?")  // Ejecuta todos los días a las 9:00 AM
    public void checkLowStockAndSendAlerts() {
        List<Product> lowStockProducts = productRepository.findByQuantityLessThan(THRESHOLD);

        if (!lowStockProducts.isEmpty()) {
            sendLowStockAlert(lowStockProducts);
        }
    }

    // Enviar alerta por correo
    private void sendLowStockAlert(List<Product> lowStockProducts) {
        StringBuilder messageBody = new StringBuilder("Productos con inventario bajo:\n");

        for (Product product : lowStockProducts) {
            messageBody.append("ID: ").append(product.getId())
                    .append(", Nombre: ").append(product.getName())
                    .append(", Cantidad: ").append(product.getQuantity())
                    .append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("correo_responsable@example.com");  // Reemplazar con correo real
        message.setSubject("Alerta: Inventarios Bajos");
        message.setText(messageBody.toString());
        message.setFrom("tu_correo@gmail.com");

        mailSender.send(message);
    }
}
