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

### 🧪 Notas adicionales

- El PDF se guarda localmente con un nombre dinámico: `accreditation_{id}.pdf`.
- El sistema imprime trazas en consola para ayudar al monitoreo durante desarrollo o pruebas.
- El correo está configurado con un remitente genérico de pruebas y puede adaptarse fácilmente a un entorno productivo.
