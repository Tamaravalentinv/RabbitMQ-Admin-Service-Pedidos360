package cl.duoc.pedidos360.rabbitmqadmin.service;

import cl.duoc.pedidos360.rabbitmqadmin.service.RabbitAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RabbitAdminServiceTest {

    private RabbitAdmin rabbitAdmin;
    private RabbitAdminService service;

    @BeforeEach
    void configurar() {
        rabbitAdmin = mock(RabbitAdmin.class);
        service = new RabbitAdminService(rabbitAdmin);
    }

    @Test
    void eliminarExchangeDebeLlamarRabbitAdmin() {
        service.eliminarExchange("exchange.prueba");

        verify(rabbitAdmin).deleteExchange("exchange.prueba");
    }

    @Test
    void eliminarColaDebeLlamarRabbitAdmin() {
        service.eliminarCola("cola.prueba");

        verify(rabbitAdmin).deleteQueue("cola.prueba");
    }

    @Test
    void crearExchangeConTipoInvalidoDebeLanzarExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.crearExchange("exchange.prueba", "invalido")
        );

        verifyNoInteractions(rabbitAdmin);
    }
}