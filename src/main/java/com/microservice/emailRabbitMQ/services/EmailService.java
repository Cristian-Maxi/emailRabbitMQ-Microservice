package com.microservice.emailRabbitMQ.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.microservice.emailRabbitMQ.config.RabbitMQConfig;
import com.microservice.emailRabbitMQ.utils.AccreditationCreatedEvent;
import com.microservice.emailRabbitMQ.utils.UserRegisteredEvent;
import jakarta.mail.MessagingException;

import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleAccreditationCreatedEvent(AccreditationCreatedEvent event) {
        System.out.println("Event received: " + event);
        generateAccreditationPdf(event);
        sendEmailWithPdf(event);
    }

    private void generateAccreditationPdf(AccreditationCreatedEvent event) {
        try (PdfWriter writer = new PdfWriter("accreditation_" + event.getId() + ".pdf")) {
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Accreditation Details").setFontSize(20));
            document.add(new Paragraph("Accreditation ID: " + event.getId()));
            document.add(new Paragraph("Amount: $" + String.format("%.2f", event.getAmount())));
            document.add(new Paragraph("Point of Sale ID: " + event.getPointOfSaleId()));
            document.add(new Paragraph("Point of Sale Name: " + event.getPointOfSaleName()));
            document.add(new Paragraph("Received At: " + event.getReceivedAt()));

            document.close();
            System.out.println("PDF generated successfully at accreditation_" + event.getId() + ".pdf");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating PDF: " + e.getMessage());
        }
    }
    private void sendEmailWithPdf(AccreditationCreatedEvent event) {
        String recipientEmail = "cristian@outlook";
        String subject = "Accreditation Confirmation - ID: " + event.getId();
        String body = "Attached is the PDF for your recent accreditation.";

        String pdfFilePath = "accreditation_" + event.getId() + ".pdf";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body);

            File pdfFile = new File(pdfFilePath);
            helper.addAttachment(pdfFile.getName(), pdfFile);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        sendWelcomeEmail(event.getEmail(), event.getName(), event.getLastname());
    }

    private void sendWelcomeEmail(String email, String name, String lastname) {
        String subject = "Welcome to Our Service!";
        String body = "Hi " + name + " " + lastname + ",\n\nThank you for registering with us. We are excited to have you on board!\n\nBest regards,\nThe Team";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
            System.out.println("Welcome email sent to " + email);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending welcome email: " + e.getMessage());
        }
    }
}