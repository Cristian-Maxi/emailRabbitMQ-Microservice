# 🧩 Proyecto de Microservicios con Spring Boot

Este proyecto es una implementación de una arquitectura de microservicios basada en Spring Boot. Utiliza tecnologías modernas como Redis, RabbitMQ y PostgreSQL para resolver problemas empresariales comunes de manera escalable, eficiente y modular.

## 🔧 Tecnologías Utilizadas

- Java 21
- Spring Boot 3.4.4
- Maven
- PostgreSQL
- Redis
- RabbitMQ
- Eureka Server (Service Discovery)
- Spring Cloud Gateway
- MapStruct
- Spring Data JPA
- Spring Validation
- Java Mail Sender
- ITextPDF
- Mailtrap
- Docker / Podman
- Postman / DBBeaver
- Open Api (Swagger)
- Junit / Mockito
- Jacoco + informe en HTML
- SonarQube (Revisión de cobertura)

## 📌 Microservicio: EmailRabbitMQs

Este microservicio está orientado al consumo de eventos del sistema, específicamente relacionados con acreditaciones creadas. Su principal responsabilidad es recibir eventos a través de **RabbitMQ**, generar un **PDF** con los datos de la acreditación, y enviarlo automáticamente por **correo electrónico** como archivo adjunto al destinatario correspondiente.

---

### ⚙️ Tecnologías utilizadas

- **Spring Boot**: Framework principal del microservicio.
- **Spring AMQP (RabbitMQ)**: Permite la recepción de eventos asincrónicos desde la cola `accreditation_created_queue`.
- **JavaMailSender (Spring Mail)**: Facilita el envío de correos electrónicos.
- **iText 7**: Utilizado para la generación dinámica de archivos PDF.
- **Jackson**: Conversión de mensajes JSON en objetos Java.
- **Mailtrap (modo sandbox)**: Entorno seguro para pruebas de envío de correos electrónicos.

---

### 🧩 Principales funcionalidades

#### 📥 Recepción de eventos
El servicio está suscrito a la cola `accreditation_created_queue` mediante la anotación `@RabbitListener`. Al recibir un evento del tipo `AccreditationCreatedEvent`, dispara un proceso de generación de PDF y envío de email.

#### 📄 Generación de PDF dinámico
Usando iText, se crea un archivo PDF que contiene los detalles de la acreditación como:

- ID
- Monto
- ID del punto de venta
- Nombre del punto de venta
- Fecha de recepción

#### ✉️ Envío de correo electrónico
El PDF generado se adjunta a un correo que se envía al destinatario (`cristian@outlook`, en este ejemplo).  
El correo incluye un asunto descriptivo y un cuerpo con el mensaje correspondiente.

#### 🔧 Configuración de RabbitMQ
Se define la cola y un `RabbitListenerContainerFactory` con un `Jackson2JsonMessageConverter` que permite deserializar eventos JSON a objetos Java.

#### 📬 Configuración de envío de correos
Se configura un `JavaMailSender` con Mailtrap (host, puerto, credenciales), habilitando el protocolo SMTP con autenticación y TLS.

---

## 📨 Muestra del PDF que se le envia al email del usuario una vez creada la acreditación

![Captura de pantalla 2025-04-14 193700](https://github.com/user-attachments/assets/2471d590-b0c4-4a55-b9ce-8b5439f10db3)

---

### 🧪 Notas adicionales

- El PDF se guarda localmente con un nombre dinámico: `accreditation_{id}.pdf`.
- El sistema imprime trazas en consola para ayudar al monitoreo durante desarrollo o pruebas.
- El correo está configurado con un remitente genérico de pruebas y puede adaptarse fácilmente a un entorno productivo.

## 🚀 Levantar el Proyecto con Podman Compose

### 🔸 Pre-requisitos

- Tener instalado `Podman` y `Podman Compose`.
- Crear un archivo `.env` con las siguientes variables:
```.env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=myappdb

SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

SECRET_KEY=B374A26A71421437AA024E4FADD5B478FDFF1A8EA6FF12F6FB65AF2720B59CCF
```

- docker-compose.yml:

```
version: '3.8'

services:
  postgres:
    image: postgres:16
    container_name: springboot_postgres
    restart: always
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      POSTGRES_DB: ${POSTGRES_DB}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - spring-network

  redis:
    image: redis:7.2
    container_name: redis_cache
    restart: always
    ports:
      - "6379:6379"
    networks:
      - spring-network

  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    restart: always
    ports:
      - "5672:5672"   # Puerto de conexión para microservicios
      - "15672:15672" # Puerto de panel web de administración
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    networks:
      - spring-network

  eureka:
    build: ./eureka_server
    container_name: eureka_server
    ports:
      - "8761:8761"
    networks:
      - spring-network

  gateway:
    build: ./cloud-gateway
    container_name: gateway_service
    ports:
      - "8080:8080"
    depends_on:
      - eureka
    environment:
      - SECRET_KEY=${SECRET_KEY}
    networks:
      - spring-network

  pointsalecost:
    build: ./Point_of_Sale_Cost-Microservice
    container_name: pointsalecost_service
    depends_on:
      - postgres
      - redis
      - eureka
    environment:
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
    networks:
      - spring-network

  accreditations:
    build: ./Accreditation-Microservice
    container_name: accreditations_service
    depends_on:
      - postgres
      - redis
      - eureka
      - rabbitmq
    environment:
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
    networks:
      - spring-network

  usermicroservice:
    build: ./Users-Microservice
    container_name: usermicroservice_service
    depends_on:
      - postgres
      - redis
      - eureka
      - rabbitmq
    environment:
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
      - SECRET_KEY=${SECRET_KEY}
    networks:
      - spring-network

  emailrabbitmq:
    build: ./emailRabbitMQ-Microservice
    container_name: email_rabbitmq_service
    depends_on:
      - rabbitmq
    environment:
      - SECRET_KEY=${SECRET_KEY}
    ports:
      - "8084:8084"
    networks:
      - spring-network

volumes:
  postgres_data:

networks:
  spring-network:
    driver: bridge

```

- Comando para construir y levantar con Podman Compose:
`podman compose up --build`

## ✅ Estructura de la Carpeta

![Estructura de carpetas](https://github.com/user-attachments/assets/b6ff7ad2-9a19-40d1-93d3-4d98b37054b8)
