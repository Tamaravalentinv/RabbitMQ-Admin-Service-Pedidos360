
package cl.duoc.pedidos360.rabbitmqadmin.service;

import org.springframework.amqp.core.Binding;
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

    private void validarNombre(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                "El campo '" + campo + "' es obligatorio y no puede estar vacío."
            );
        }
    }

    public void crearCola(String nombre, boolean durable) {
        validarNombre(nombre, "nombre de la cola");

        Queue queue = new Queue(nombre, durable);
        rabbitAdmin.declareQueue(queue);
    }

    public void eliminarCola(String nombre) {
        validarNombre(nombre, "nombre de la cola");

        rabbitAdmin.deleteQueue(nombre);
    }

    public void crearExchange(String nombre, String tipo) {
        validarNombre(nombre, "nombre del exchange");
        validarNombre(tipo, "tipo de exchange");

        if ("direct".equalsIgnoreCase(tipo)) {
            rabbitAdmin.declareExchange(
                new DirectExchange(nombre, true, false)
            );
            return;
        }

        if ("topic".equalsIgnoreCase(tipo)) {
            rabbitAdmin.declareExchange(
                new TopicExchange(nombre, true, false)
            );
            return;
        }

        throw new IllegalArgumentException(
            "Tipo de exchange no válido. Utilice 'direct' o 'topic'."
        );
    }

    public void eliminarExchange(String nombre) {
        validarNombre(nombre, "nombre del exchange");

        rabbitAdmin.deleteExchange(nombre);
    }

    public void crearBinding(
            String cola,
            String exchange,
            String routingKey) {

        validarNombre(cola, "cola");
        validarNombre(exchange, "exchange");
        validarNombre(routingKey, "routingKey");

        Binding binding = new Binding(
            cola,
            Binding.DestinationType.QUEUE,
            exchange,
            routingKey,
            null
        );

        rabbitAdmin.declareBinding(binding);
    }

    public void eliminarBinding(
            String cola,
            String exchange,
            String routingKey) {

        validarNombre(cola, "cola");
        validarNombre(exchange, "exchange");
        validarNombre(routingKey, "routingKey");

        Binding binding = new Binding(
            cola,
            Binding.DestinationType.QUEUE,
            exchange,
            routingKey,
            null
        );

        rabbitAdmin.removeBinding(binding);
    }
}
