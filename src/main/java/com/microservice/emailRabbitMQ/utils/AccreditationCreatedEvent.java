package com.microservice.emailRabbitMQ.utils;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccreditationCreatedEvent {
    private Long id;
    private String email;
    private Double amount;
    private Long pointOfSaleId;
    private String pointOfSaleName;
    private LocalDateTime receivedAt;
}