package cl.duoc.pedidos360.rabbitmqadmin.controller;

import cl.duoc.pedidos360.rabbitmqadmin.dto.BindingRequest;
import cl.duoc.pedidos360.rabbitmqadmin.dto.ExchangeRequest;
import cl.duoc.pedidos360.rabbitmqadmin.dto.QueueRequest;
import cl.duoc.pedidos360.rabbitmqadmin.service.RabbitAdminService;

import jakarta.validation.Valid;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/admin/rabbitmq")
public class RabbitMqAdminController {

    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdminService rabbitAdminService;

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
            RabbitTemplate rabbitTemplate,
            RabbitAdminService rabbitAdminService) {

        this.rabbitAdmin = rabbitAdmin;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdminService = rabbitAdminService;
    }

    // =========================================================
    // CONSULTAR COLAS
    // =========================================================

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

    // =========================================================
    // ADMINISTRAR COLAS
    // =========================================================

    @PostMapping("/queues")
    public Map<String, Object> crearCola(
            @Valid @RequestBody QueueRequest request) {

        rabbitAdminService.crearCola(
                request.getNombre(),
                request.isDurable()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Cola creada correctamente");
        response.put("nombre", request.getNombre());
        response.put("durable", request.isDurable());

        return response;
    }

    @DeleteMapping("/queues/{name}")
    public Map<String, Object> eliminarCola(
            @PathVariable String name) {

        rabbitAdminService.eliminarCola(name);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Cola eliminada correctamente");
        response.put("nombre", name);

        return response;
    }

    // =========================================================
    // ADMINISTRAR EXCHANGES
    // =========================================================

    @PostMapping("/exchanges")
    public Map<String, Object> crearExchange(
            @Valid @RequestBody ExchangeRequest request) {

        rabbitAdminService.crearExchange(
                request.getNombre(),
                request.getTipo()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Exchange creado correctamente");
        response.put("nombre", request.getNombre());
        response.put("tipo", request.getTipo());

        return response;
    }

    @DeleteMapping("/exchanges/{name}")
    public Map<String, Object> eliminarExchange(
            @PathVariable String name) {

        rabbitAdminService.eliminarExchange(name);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Exchange eliminado correctamente");
        response.put("nombre", name);

        return response;
    }

    // =========================================================
    // ADMINISTRAR BINDINGS
    // =========================================================

    @PostMapping("/bindings")
    public Map<String, Object> crearBinding(
            @Valid @RequestBody BindingRequest request) {

        rabbitAdminService.crearBinding(
                request.getCola(),
                request.getExchange(),
                request.getRoutingKey()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Binding creado correctamente");
        response.put("cola", request.getCola());
        response.put("exchange", request.getExchange());
        response.put("routingKey", request.getRoutingKey());

        return response;
    }

    @DeleteMapping("/bindings")
    public Map<String, Object> eliminarBinding(
            @Valid @RequestBody BindingRequest request) {

        rabbitAdminService.eliminarBinding(
                request.getCola(),
                request.getExchange(),
                request.getRoutingKey()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("estado", "Binding eliminado correctamente");
        response.put("cola", request.getCola());
        response.put("exchange", request.getExchange());
        response.put("routingKey", request.getRoutingKey());

        return response;
    }

    // =========================================================
    // DEAD LETTER QUEUE
    // =========================================================

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