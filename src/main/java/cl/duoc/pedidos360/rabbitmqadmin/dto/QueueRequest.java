package cl.duoc.pedidos360.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;

public class QueueRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String nombre;

    private boolean durable = true;

    public QueueRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }
}