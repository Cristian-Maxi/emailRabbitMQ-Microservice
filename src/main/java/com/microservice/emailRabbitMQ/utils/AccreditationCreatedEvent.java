package com.microservice.emailRabbitMQ.utils;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
public class AccreditationCreatedEvent {
    private Long id;
    private String email;
    private Double amount;
    private Long pointOfSaleId;
    private String pointOfSaleName;
    private LocalDateTime receivedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getPointOfSaleName() {
        return pointOfSaleName;
    }

    public void setPointOfSaleName(String pointOfSaleName) {
        this.pointOfSaleName = pointOfSaleName;
    }

    public Long getPointOfSaleId() {
        return pointOfSaleId;
    }

    public void setPointOfSaleId(Long pointOfSaleId) {
        this.pointOfSaleId = pointOfSaleId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}