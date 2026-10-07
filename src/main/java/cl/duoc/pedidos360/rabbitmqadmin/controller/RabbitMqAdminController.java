package cl.duoc.pedidos360.rabbitmqadmin.controller;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/admin/rabbitmq")
public class RabbitMqAdminController {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;

    @Value("${pedidos360.rabbitmq.queue}")
    private String queue;

    @Value("${pedidos360.rabbitmq.dead-letter-queue}")
    private String deadLetterQueue;

    @Value("${pedidos360.rabbitmq.exchange}")
    private String exchange;

    @Value("${pedidos360.rabbitmq.routing-key}")
    private String routingKey;

    public RabbitMqAdminController(
            RabbitAdmin rabbitAdmin,
            RabbitTemplate rabbitTemplate) {

        this.rabbitAdmin = rabbitAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/queues")
    public Map<String, Object> getQueues() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "RabbitMQ conectado");
        response.put("colaPrincipal", getQueueInfo(queue));
        response.put("deadLetterQueue", getQueueInfo(deadLetterQueue));

        return response;
    }

    private Map<String, Object> getQueueInfo(String queueName) {

        Properties properties = rabbitAdmin.getQueueProperties(queueName);

        Map<String, Object> info = new LinkedHashMap<>();

        info.put("nombre", queueName);

        if (properties == null) {
            info.put("existe", false);
            return info;
        }

        info.put("existe", true);
        info.put(
                "mensajes",
                properties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT)
        );
        info.put(
                "consumidores",
                properties.get(RabbitAdmin.QUEUE_CONSUMER_COUNT)
        );

        return info;
    }

    @DeleteMapping("/dlq")
    public Map<String, Object> purgeDeadLetterQueue() {

        rabbitAdmin.purgeQueue(deadLetterQueue);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "DLQ vaciada correctamente");
        response.put("cola", deadLetterQueue);

        return response;
    }
    @PostMapping("/dlq/reprocess")
public Map<String, Object> reprocessDeadLetterMessage() {

    Map<String, Object> response = new LinkedHashMap<>();

    Message message = rabbitTemplate.receive(deadLetterQueue);

    if (message == null) {
        response.put("estado", "No hay mensajes en la DLQ");
        response.put("reprocesado", false);
        return response;
    }

    rabbitTemplate.send(exchange, routingKey, message);

    response.put("estado", "Mensaje reprocesado correctamente");
    response.put("reprocesado", true);
    response.put("origen", deadLetterQueue);
    response.put("destino", exchange);
    response.put("routingKey", routingKey);

    return response;
}
}