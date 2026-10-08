# RabbitMQ – Pedidos360

## 1. Descripción

Pedidos360 implementa comunicación asíncrona mediante RabbitMQ para notificar la creación de pedidos a diferentes microservicios.

Esta arquitectura permite desacoplar los servicios y procesar eventos de forma independiente.

## 2. Arquitectura

La integración incluye los siguientes componentes:

- **Pedidos Service:** publica eventos de creación de pedidos y consume eventos para simular su confirmación.
- **Usuarios Service:** recibe eventos de pedidos y simula notificaciones al usuario.
- **RabbitMQ Admin Service:** permite consultar y administrar colas, exchanges, bindings y mensajes de la DLQ.
- **RabbitMQ:** intermediario encargado de distribuir los mensajes.

### Flujo de comunicación

1. Se publica un evento `PedidoCreadoEvent`.
2. RabbitMQ recibe el evento mediante el exchange `pedidos360.events`.
3. El evento se distribuye a las colas configuradas.
4. Pedidos Service y Usuarios Service procesan el evento de manera independiente.
5. Si el procesamiento es exitoso, el consumidor confirma el mensaje mediante ACK.
6. Si ocurre un error, el mensaje puede rechazarse mediante NACK y enviarse a una Dead Letter Queue (DLQ).

## 3. Configuración de RabbitMQ

RabbitMQ se ejecuta localmente mediante Docker.

- Puerto AMQP: `5672`
- Panel de administración: `http://localhost:15672`
- Exchange principal: `pedidos360.events`
- Routing key: `pedido.creado`

### Colas utilizadas

| Cola | Función |
|---|---|
| `pedidos.pedido-creado.q` | Procesamiento de eventos en Pedidos Service |
| `pedidos.pedido-creado.dlq` | Mensajes fallidos de Pedidos Service |
| `usuarios.pedido-creado.q` | Procesamiento de eventos en Usuarios Service |
| `usuarios.pedido-creado.dlq` | Mensajes fallidos de Usuarios Service |

## 4. Manejo de mensajes

### ACK

Cuando un evento es válido y se procesa correctamente, el consumidor envía una confirmación ACK a RabbitMQ.

### NACK

Cuando ocurre un error de procesamiento, el consumidor puede enviar NACK sin reencolar el mensaje.

### DLQ

Las Dead Letter Queues almacenan mensajes que no pudieron procesarse correctamente, permitiendo revisar los errores y evitar que se pierdan silenciosamente.

## 5. Administración de RabbitMQ

El microservicio RabbitMQ Admin permite realizar operaciones administrativas sobre la infraestructura de mensajería.

Entre sus funcionalidades se encuentran:

- Consultar el estado de las colas.
- Crear y eliminar colas.
- Crear y eliminar exchanges.
- Crear y eliminar bindings.
- Consultar y limpiar mensajes de la DLQ.
- Reprocesar mensajes fallidos mediante el endpoint correspondiente.

## 6. Pruebas realizadas

### Prueba de procesamiento exitoso

Se publicó un evento válido con `pedidoId=100`.

**Resultado:** ambos consumidores procesaron el evento y registraron una confirmación ACK.

### Prueba de error

Se publicó un evento con datos inválidos.

**Resultado:** los consumidores rechazaron el evento y se verificó su llegada a las respectivas DLQ.

### Pruebas unitarias

Se ejecutaron pruebas automatizadas utilizando JUnit 5 y Mockito.

| Microservicio | Pruebas exitosas |
|---|---:|
| Usuarios Service | 3 |
| RabbitMQ Admin | 3 |
| Pedidos Service | 2 pruebas del consumidor |

Los resultados de las pruebas ejecutadas fueron satisfactorios.

## 7. Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring AMQP
- RabbitMQ
- Docker
- Maven
- JUnit 5
- Mockito
- Spring Boot Actuator

## 8. Conclusión

La implementación de RabbitMQ permite que Pedidos360 distribuya eventos de creación de pedidos entre distintos microservicios.

El uso de ACK, NACK y DLQ proporciona mecanismos de confirmación y tratamiento de errores, mientras que RabbitMQ Admin facilita las tareas de administración y supervisión.

Las pruebas realizadas validaron el procesamiento exitoso de mensajes y el manejo de eventos inválidos.