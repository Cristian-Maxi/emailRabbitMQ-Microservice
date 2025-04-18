package com.microservice.emailRabbitMQ.utils;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    private String email;
    private String name;
    private String lastname;
}
