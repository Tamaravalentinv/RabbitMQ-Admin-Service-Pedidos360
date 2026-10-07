package cl.duoc.pedidos360.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;

public class BindingRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String cola;

    @NotBlank(message = "El nombre del exchange es obligatorio")
    private String exchange;

    @NotBlank(message = "El routing key es obligatorio")
    private String routingKey;

    public BindingRequest() {
    }

    public String getCola() {
        return cola;
    }

    public void setCola(String cola) {
        this.cola = cola;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}