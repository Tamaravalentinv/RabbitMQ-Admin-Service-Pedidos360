package cl.duoc.pedidos360.rabbitmqadmin.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void crearCola(String nombre, boolean durable) {
        Queue queue = new Queue(nombre, durable);
        rabbitAdmin.declareQueue(queue);
    }

    public void eliminarCola(String nombre) {
        rabbitAdmin.deleteQueue(nombre);
    }

    public void crearExchange(String nombre, String tipo) {

        if ("direct".equalsIgnoreCase(tipo)) {
            rabbitAdmin.declareExchange(new DirectExchange(nombre, true, false));
            return;
        }

        if ("topic".equalsIgnoreCase(tipo)) {
            rabbitAdmin.declareExchange(new TopicExchange(nombre, true, false));
            return;
        }

        throw new IllegalArgumentException(
                "Tipo de exchange no válido. Utilice 'direct' o 'topic'.");
    }

    public void eliminarExchange(String nombre) {
        rabbitAdmin.deleteExchange(nombre);
    }

    public void crearBinding(
            String cola,
            String exchange,
            String routingKey) {

        Queue queue = new Queue(cola, true);
        TopicExchange topicExchange = new TopicExchange(exchange, true, false);

        Binding binding = BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(routingKey);

        rabbitAdmin.declareBinding(binding);
    }

    public void eliminarBinding(
            String cola,
            String exchange,
            String routingKey) {

        Binding binding = new Binding(
                cola,
                Binding.DestinationType.QUEUE,
                exchange,
                routingKey,
                null);

        rabbitAdmin.removeBinding(binding);
    }
}