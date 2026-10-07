package cl.duoc.pedidos360.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ExchangeRequest {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    private String nombre;

    @NotBlank(message = "El tipo de exchange es obligatorio")
    @Pattern(
        regexp = "^(?i)(direct|topic)$",
        message = "El tipo de exchange debe ser 'direct' o 'topic'"
    )
    private String tipo;

    public ExchangeRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}