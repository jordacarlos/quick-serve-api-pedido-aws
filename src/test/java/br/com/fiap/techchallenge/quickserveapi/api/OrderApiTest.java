package br.com.fiap.techchallenge.quickserveapi.api;

import br.com.fiap.techchallenge.quickserveapi.application.handler.api.Order;
import br.com.fiap.techchallenge.quickserveapi.application.handler.controllers.OrderController;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderHelper;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.APROVADO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.EM_PREPARACAO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Order.class)
public class OrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderController orderController;

    @MockBean
    private OrderCase orderCase;

    private final String urlOrder = "/quick_service/orders";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Inicializando MockMvc com o controlador REST
        mockMvc = MockMvcBuilders.standaloneSetup(new Order(orderController)).build();
    }

    @Nested
    class RegistrarPedido {
        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var pedido = BuildOrderHelper.buildOrder();

            when(orderController.save(any(OrderPostEntity.class)))
                    .thenAnswer(i -> BuildOrderResponseDTO.buildOrderDTO());

            // Act & Assert
            mockMvc.perform(post(urlOrder).contentType(MediaType.APPLICATION_JSON).content(asJsonString(pedido)))
                    .andExpect(status().isCreated());

            // Verifica se o método do controlador foi chamado
            verify(orderController, times(1)).save(any(OrderPostEntity.class));
        }
        @Test
        void deveGerarExececao_QuandoRegistrarPedido_PayloadXML() throws Exception {
            String xmlPayload = "<Pedido><usuario>Ana</usuario><conteudo>Pedido de X-Tudo</conteudo></Pedido>";

            mockMvc.perform(post(urlOrder)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(orderController,never()).save(any(OrderPostEntity.class));
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedido() {
            // Configura os dados de entrada e o esperado
            var id = "1";
            var esperado = new PaymentStatusDTO(1L,"PENDENTE");

            esperado.setOrderId(Long.valueOf(id));
            esperado.setPaymentStatus(String.valueOf(APROVADO));


            // Mocka o comportamento do controller para retornar o esperado
            when(orderController.checkPaymentStatus(Long.valueOf(id)))
                    .thenReturn(new PaymentStatusDTO(Long.valueOf(id), "APROVADO"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarPedido_IdNaoExiste() throws Exception {
            var id = "8818064";

            // Simula a exceção sendo lançada no controlador
            when(orderController.findById(Long.valueOf(id))).thenThrow(NotFoundException.class);

            mockMvc.perform(get(urlOrder+"/{id}", id))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(orderController, times(1)).findById(Long.valueOf(id));
        }


    }

    @Nested
    class BuscarPagamento{
        @Test
        void devePermitirBuscarStatusPagamento() throws Exception {
            var id = "1";

            when(orderController.checkPaymentStatus(Long.valueOf(id)))
                    .thenReturn(new PaymentStatusDTO(1L, "RECEBIDO"));

            mockMvc.perform(get(urlOrder + "/payment/{id}", id))
                    .andExpect(status().isOk());

            verify(orderController, times(1)).checkPaymentStatus(Long.valueOf(id));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarStatusPagamento_IdNaoExiste() throws Exception {
            var id = "8818064";

            // Simula a exceção sendo lançada no controlador
            when(orderController.checkPaymentStatus(Long.valueOf(id))).thenThrow(NotFoundException.class);

            mockMvc.perform(get(urlOrder+"/payment/{id}",id))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(orderController, times(1)).checkPaymentStatus(Long.valueOf(id));
        }
    }

    @Nested
    class AlterarPedido{
        @Test
        void devePermitirAlterarStatusPedido() throws Exception {
            // Definindo o ID e status do pedido
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setStatus(String.valueOf(EM_PREPARACAO));

            // Simula o comportamento do controlador para o método findById
            when(orderController.findById(Long.valueOf(id))).thenReturn(pedido);

            // Simula o comportamento do controlador para o método updateStatus
            when(orderController.updateStatus(pedido)).thenAnswer(i -> BuildOrderResponseDTO.buildOrderDTO());


            // Realiza a requisição PUT e verifica se o status HTTP é 202 Accepted
            mockMvc.perform(put(urlOrder + "/{id}/{status}", id, EM_PREPARACAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido)))
                    .andExpect(status().isAccepted());

            // Verifica se o método foi chamado uma vez
            verify(orderController, times(1)).updateStatus(pedido);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarStatusPedido_IdNaoExiste() throws Exception {
            var id = "8818064"; // Um ID que não existe
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            // Simula o comportamento do controlador para o método findById
            when(orderController.findById(Long.valueOf(id))).thenReturn(null); // Pedido não encontrado

            // Realiza a requisição PUT e verifica se o status HTTP é 400 Bad Request
            mockMvc.perform(put(urlOrder + "/{id}/{status}", id, "EM_PREPARACAO") // O status pode ser qualquer valor de sua enum
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido))) // Passa o pedido no corpo da requisição
                    .andExpect(status().isBadRequest()); // Espera o status HTTP 400 (Bad Request)

            // Verifica se o método findById foi chamado uma vez
            verify(orderController, times(1)).findById(Long.valueOf(id));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarStatusPedido_PayloadXML() throws Exception {
            // Exemplo de payload XML para alteração de status do pedido
            String xmlPayload = "<AlteracaoStatusPedido><id>123456</id><status>EM_PREPARACAO</status></AlteracaoStatusPedido>";

            // Realiza a requisição POST e espera que o status HTTP seja 415 (Unsupported Media Type)
            mockMvc.perform(put(urlOrder + "/{id}/{status}", "123456", "EM_PREPARACAO")
                            .contentType(MediaType.APPLICATION_XML)  // Define o tipo de mídia como XML
                            .content(xmlPayload))  // Passa o payload XML no corpo da requisição
                    .andExpect(status().isUnsupportedMediaType());  // Espera o status HTTP 415 (Unsupported Media Type)

            // Verifica que o método do controller não foi chamado
            verify(orderController, never()).updateStatus(any(OrderResponseDTO.class));
        }

        @Test
        void devePermitirAlterarPagamentoPedido() throws Exception {
            // Definindo o ID e status do pedido
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setPaymentStatus(String.valueOf(APROVADO));

            // Simula o comportamento do controlador para o método updateStatus
            when(orderController.paymentApprover(pedido)).thenAnswer(i -> BuildOrderResponseDTO.buildOrderDTO());

            // Simula o comportamento do controlador para o método findById
            when(orderController.findById(Long.valueOf(id))).thenReturn(pedido);


            // Realiza a requisição PUT e verifica se o status HTTP é 202 Accepted
            mockMvc.perform(put(urlOrder + "/payment-approver/{id}/{statusPayment}", id, APROVADO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido)))
                    .andExpect(status().isAccepted());

            // Verifica se o método foi chamado uma vez
            verify(orderController, times(1)).paymentApprover(pedido);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPagamentoPedido_IdNaoExiste() throws Exception {
            var id = "8818064"; // Um ID que não existe
            var status = OrderPaymentStatusEnum.APROVADO; // Status de pagamento

            // Criação do pedido de teste
            var pedido = BuildOrderResponseDTO.buildOrderDTO();
            pedido.setId(Long.valueOf(id));
            pedido.setPaymentStatus(String.valueOf(status));

            // Simula que o método findById retorna null, indicando que o pedido não foi encontrado
            when(orderController.findById(Long.valueOf(id))).thenReturn(null);

            // Realiza a requisição PUT e espera que o status HTTP seja 400 (Bad Request)
            mockMvc.perform(put(urlOrder + "/payment-approver/{id}/{statusPayment}", id, status)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(pedido))) // Passa o pedido no corpo da requisição
                    .andDo(print())
                    .andExpect(status().isBadRequest()); // Espera o status HTTP 400 (Bad Request)

            // Verifica se o método findById foi chamado uma vez
            verify(orderController, times(1)).findById(Long.valueOf(id));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPagamentoPedido_PayloadXML() throws Exception {
            // Exemplo de payload XML para alteração de status do pedido
            String xmlPayload = "<AlteracaoStatusPedido><id>123456</id><status>APROVADO</status></AlteracaoStatusPedido>";

            // Realiza a requisição POST e espera que o status HTTP seja 415 (Unsupported Media Type)
            mockMvc.perform(put(urlOrder + "/payment-approver/{id}/{statusPayment}", "123456", "APROVADO")
                            .contentType(MediaType.APPLICATION_XML)  // Define o tipo de mídia como XML
                            .content(xmlPayload))  // Passa o payload XML no corpo da requisição
                    .andExpect(status().isUnsupportedMediaType());  // Espera o status HTTP 415 (Unsupported Media Type)

            // Verifica que o método do controller não foi chamado
            verify(orderController, never()).updateStatus(any(OrderResponseDTO.class));
        }

    }

    @Nested
    class ListarPedidos{
        @Test
        void devePermitirListarPedidos() throws Exception {
            // Gera dinamicamente a lista de OrderResponseDTOs
            List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();

            // Supondo que BuildOrderResponseDTO tenha um método para criar OrderResponseDTOs
            // Adicionando alguns pedidos à lista
            orderResponseDTOList.add(BuildOrderResponseDTO.buildOrderDTO());  // Adiciona um pedido
            orderResponseDTOList.add(BuildOrderResponseDTO.buildOrderDTO());  // Adiciona outro pedido

            // Mock da resposta para o método findAll() no controlador
            when(orderController.findAll()).thenReturn(orderResponseDTOList);  // Retorna a lista de pedidos

            // Realiza a requisição GET para listar pedidos
            mockMvc.perform(get(urlOrder + "/"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
        @Test
        void devePermitirListarPedidosComOrdenacao() throws Exception {
            // Gera dinamicamente a lista de OrderResponseDTOs com diferentes status para testar a ordenação
            List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();

            // Criando pedidos com diferentes status para testar a ordenação
            orderResponseDTOList.add(BuildOrderResponseDTO.buildOrderDTO());
            orderResponseDTOList.add(BuildOrderResponseDTO.buildOrderDTO());
            orderResponseDTOList.add(BuildOrderResponseDTO.buildOrderDTO());

            // Mock da resposta para o método listByFiltersWithSorting no controlador
            when(orderController.findAllSorted("order_id ASC")).thenReturn(orderResponseDTOList);  // Ordena por "PRONTO"

            // Realiza a requisição GET para listar pedidos com ordenação (passando o parâmetro "PRONTO" como exemplo)
            mockMvc.perform(get(urlOrder + "/list"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }


        @Test
        void testListByFilters() throws Exception {
            MvcResult result = mockMvc.perform(get(urlOrder + "/list"))
                    .andExpect(status().isOk())
                    .andReturn(); // Obtém o resultado da resposta

            // Imprime a resposta no console para análise
            System.out.println(result.getResponse().getContentAsString());

            // Agora você pode validar a resposta
            mockMvc.perform(get(urlOrder + "/list"))
                    .andExpect(status().isOk());
        }
    }
    public static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
