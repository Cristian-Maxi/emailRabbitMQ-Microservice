package com.microservice.emailRabbitMQ.emailService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.microservice.emailRabbitMQ.services.EmailService;
import com.microservice.emailRabbitMQ.utils.AccreditationCreatedEvent;
import com.microservice.emailRabbitMQ.utils.UserRegisteredEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleAccreditationCreatedEvent() throws IOException, MessagingException {
        AccreditationCreatedEvent event = new AccreditationCreatedEvent(1L, "test@example.com", 100.0, 1L, "Terminal A", LocalDateTime.now());

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.handleAccreditationCreatedEvent(event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
        File pdfFile = new File("accreditation_" + event.getId() + ".pdf");
        assertTrue(pdfFile.exists());
        pdfFile.delete();
    }

    @Test
    public void testHandleUserRegisteredEvent() throws MessagingException {
        UserRegisteredEvent event = new UserRegisteredEvent("test@example.com", "John", "Doe");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.handleUserRegisteredEvent(event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendWelcomeEmail() throws MessagingException {
        String email = "test@example.com";
        String name = "John";
        String lastname = "Doe";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendWelcomeEmail(email, name, lastname);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendEmailWithPdf() throws MessagingException, IOException {
        AccreditationCreatedEvent event = new AccreditationCreatedEvent(1L, "test@example.com", 100.0, 1L, "Terminal A", LocalDateTime.now());

        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendEmailWithPdf(event);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
        File pdfFile = new File("accreditation_" + event.getId() + ".pdf");
        assertTrue(pdfFile.exists());
        pdfFile.delete(); // Clean up the generated PDF file
    }
}