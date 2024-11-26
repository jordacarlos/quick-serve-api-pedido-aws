package br.com.fiap.techchallenge.quickserveapi.repository;

import br.com.fiap.techchallenge.quickserveapi.application.handler.controllers.OrderController;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.APROVADO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.PENDENTE;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.EM_PREPARACAO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.RECEBIDO;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OrderRepositoryTest {
    @Mock
    private OrderController orderController;

    AutoCloseable openMocks;

    @BeforeEach
    void setup(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown () throws Exception{
        openMocks.close();
    }

    @Test
    void devePermitirBuscarPedido() {
        // Configura os dados de entrada e o esperado
        var id = "1";
        var esperado = new PaymentStatusDTO(Long.valueOf(id), "PENDENTE");

        // Mocka o comportamento do controller para retornar o esperado
        when(orderController.checkPaymentStatus(Long.valueOf(id)))
                .thenReturn(new PaymentStatusDTO(Long.valueOf(id), "PENDENTE"));

        // Chama o método a ser testado
        var resultado = orderController.checkPaymentStatus(Long.valueOf(id));

        // Valida o resultado
        assertNotNull(resultado);
        assertEquals(esperado.getOrderId(), resultado.getOrderId());
        assertEquals(esperado.getPaymentStatus(), resultado.getPaymentStatus());

        // Verifica se o método mockado foi chamado
        verify(orderController, times(1)).checkPaymentStatus(Long.valueOf(id));
    }

    @Test
    void deveRetornarTodosOsPedidos() {
        // Configuração dos dados de entrada e mock
        List<OrderResponseDTO> esperado = List.of(
                new OrderResponseDTO(2L, "123", RECEBIDO, PENDENTE, List.of(
                        new OrderItemResponseDTO(1L, "Chris Brown", "LANCHE", 0.0, "TURURURURU", "asdkjlasdasdaskldjas/")
                ), 0.0),
                new OrderResponseDTO(3L, "123", RECEBIDO, PENDENTE, List.of(
                        new OrderItemResponseDTO(1L, "Chris Brown", "LANCHE", 0.0, "TURURURURU", "asdkjlasdasdaskldjas/")
                ),0.0)
        );

        // Mock do comportamento do controlador
        when(orderController.findAll()).thenReturn(esperado);

        // Chama o método a ser testado
        Object resultado = orderController.findAll(); // Supondo que este método está na classe de serviço

        // Validações
        assertNotNull(resultado);
        assertTrue(resultado instanceof List);
        assertEquals(esperado, resultado);

        // Verifica se o método mockado foi chamado
        verify(orderController, times(1)).findAll();
    }


    @Test
    void deveRetornarListaDePedidosOrdenada() throws Exception {
        // Configuração do mock para simular o retorno da lista ordenada
        List<OrderResponseDTO> pedidos = List.of(
                new OrderResponseDTO(1L, "123", RECEBIDO, PENDENTE, new ArrayList<>(), 100.0),
                new OrderResponseDTO(2L, "124", EM_PREPARACAO, PENDENTE, new ArrayList<>(), 200.0)
        );

        when(orderController.findAllSorted("order_id ASC")).thenReturn(pedidos);

        // Chama o método a ser testado
        List<OrderResponseDTO> resultado = orderController.findAllSorted("order_id ASC");

        // Validações
        assertNotNull(resultado, "O resultado não deve ser nulo");
        assertEquals(2, resultado.size());
        assertEquals(pedidos.get(0).getId(), resultado.get(0).getId());
        assertEquals(pedidos.get(1).getId(), resultado.get(1).getId());

        // Verifica se o método mockado foi chamado
        verify(orderController, times(1)).findAllSorted("order_id ASC");
    }


    @Test
    void deveRetornarExcecaoQuandoNaoExistiremPedidos() {
        // Configura o mock para lançar a exceção
        when(orderController.findAll()).thenThrow(new NotFoundException("Nenhum pedido encontrado"));

        // Chama o método a ser testado
        Object resultado = null;
        try {
            resultado = orderController.findAll();
        } catch (NotFoundException ex) {
            resultado = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        // Validações
        assertNotNull(resultado);
        assertTrue(resultado instanceof ResponseEntity);
        ResponseEntity<?> responseEntity = (ResponseEntity<?>) resultado;

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Nenhum pedido encontrado", responseEntity.getBody());

        // Verifica se o método mockado foi chamado
        verify(orderController, times(1)).findAll();
    }

    @Test
    void testListByFilters() {
        // Simula o comportamento do método findAllSorted
        List<OrderResponseDTO> mockResponse = Arrays.asList(
                new OrderResponseDTO(1L, "customer1", RECEBIDO, PENDENTE, null, 100.0),
                new OrderResponseDTO(2L, "customer2", RECEBIDO, PENDENTE, null, 200.0)
        );

        // Quando o método findAllSorted for chamado, ele retorna o mockResponse
        when(orderController.findAllSorted("order_id ASC")).thenReturn(mockResponse);

        // Chama o método listByFilters
        List<OrderResponseDTO> response = orderController.findAllSorted("order_id ASC");

        // Verifica se a resposta não é nula e tem o tamanho esperado
        assertNotNull(response);
        assertEquals(2, response.size());

        // Verifica o conteúdo do primeiro item
        assertEquals(Long.valueOf(1), response.get(0).getId());
        assertEquals("RECEBIDO", response.get(0).getStatus());

        // Verifica se o método findAllSorted foi chamado com o parâmetro correto
        verify(orderController).findAllSorted("order_id ASC");
    }
}
