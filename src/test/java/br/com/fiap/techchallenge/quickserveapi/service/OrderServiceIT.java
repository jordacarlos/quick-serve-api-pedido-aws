package br.com.fiap.techchallenge.quickserveapi.service;


import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderResponseDTO;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.PaymentStatusDTO;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderHelper;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderResponseDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.APROVADO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.EM_PREPARACAO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@ActiveProfiles("test")
@Sql("/data.sql")
public class OrderServiceIT {

    @Autowired
    private OrderCase orderCase;

    @Nested
    class RegistrarPedido {
        @Test
        void devePermitirRegistrarPedido() {
            var pedido = BuildOrderHelper.buildOrder();

            var resultadoObtido = orderCase.save(pedido);

            //assert
            assertThat(resultadoObtido).isNotNull().isInstanceOf(OrderResponseDTO.class);
            assertThat(resultadoObtido.getId()).isNotNull();
        }
    }

    @Nested
    class BuscarPedido {
        @Test
        void devePermitirBuscarPedido() {
            var id = "2";
            var resultadoObtido = orderCase.findById(Long.valueOf(id));

            assertThat(resultadoObtido).isNotNull().isInstanceOf(OrderResponseDTO.class);
            assertThat(resultadoObtido.getStatus()).isNotNull().isEqualTo("RECEBIDO");
            assertThat(resultadoObtido.getPaymentStatus()).isNotNull().isEqualTo("PENDENTE");
        }

        @Test
        void devePermitirBuscarStatusPagamento() {
            var id = "2";
            var resultadoObtido = orderCase.checkPaymentStatus(Long.valueOf(id));
            assertThat(resultadoObtido).isNotNull().isInstanceOf(PaymentStatusDTO.class);
            assertThat(resultadoObtido.getOrderId()).isNotNull().isEqualTo(2L);
            assertThat(resultadoObtido.getPaymentStatus()).isNotNull().isEqualTo("PENDENTE");
        }
    }

    @Nested
    class AlterarPedido{
        @Test
        void devePermitirAlterarStatusPedido(){
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setStatus(String.valueOf(EM_PREPARACAO));

            var resultadoObtido = orderCase.updateStatus(pedido);
            assertThat(resultadoObtido).isNotNull().isInstanceOf(OrderResponseDTO.class);
            assertThat(resultadoObtido.getId()).isNotNull().isEqualTo(1L);
            assertThat(resultadoObtido.getStatus()).isNotNull().isEqualTo("EM_PREPARACAO");
        }

        @Test
        void devePermitirAlterarPagamentoPedido(){
            var id = "1";
            var pedido = BuildOrderResponseDTO.buildOrderDTO();

            pedido.setId(Long.valueOf(id));
            pedido.setPaymentStatus(String.valueOf(APROVADO));

            var resultadoObtido = orderCase.updatePayment(pedido);

            assertThat(resultadoObtido).isNotNull().isInstanceOf(OrderResponseDTO.class);
            assertThat(resultadoObtido.getId()).isNotNull().isEqualTo(1L);
            assertThat(resultadoObtido.getPaymentStatus()).isNotNull().isEqualTo("APROVADO");

        }
    }

    @Nested
    class ListarPedidos {
        @Test
        void devePermitirListarPedidos() {
            List<OrderResponseDTO> orderResponseDTOList = orderCase.findAll();

            assertNotNull(orderResponseDTOList, "Pedido não encontrado");
        }

        @Test
        void devePermitirListarPedidosComOrdenacao() {

            String orderList = "order_id ASC";

            List<OrderResponseDTO> orderResponseDTOList = orderCase.listByFiltersWithSorting(orderList);

            assertNotNull(orderResponseDTOList, "Pedido não encontrado");
        }
    }

    @Nested
    class GerarExcecao {
        @Test
        void deveGerarExcecao_QuandoBuscarPedido_IdNaoExiste(){
            var id = "8818064";

            assertThatThrownBy(() -> orderCase.findById(Long.valueOf((id))))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Pedido não encontrado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarStatusPedido_IdNaoExiste() {
            var id = "8818064"; // Um ID que não existe
            var pedido = BuildOrderResponseDTO.buildOrderDTO();
            pedido.setId(Long.valueOf(id));

            // Assegure-se de que a exceção é lançada corretamente
            assertThatThrownBy(() -> orderCase.updateStatus(pedido))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Pedido não encontrado");
        }
        @Test
        void deveGerarExcecao_QuandocheckPagamentoStatus_IdNaoExiste() {
            var id = "8818064"; // Um ID que não existe
            var pedido = BuildOrderResponseDTO.buildOrderDTO();
            pedido.setId(Long.valueOf(id));

            // Assegure-se de que a exceção é lançada corretamente
            assertThatThrownBy(() -> orderCase.checkPaymentStatus(Long.valueOf(id)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Pedido não encontrado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarPagamentoPedido_IdNaoExiste() {
            var id = "8818064"; // Um ID que não existe
            var pedido = BuildOrderResponseDTO.buildOrderDTO();
            pedido.setId(Long.valueOf(id));

            // Assegure-se de que a exceção é lançada corretamente
            assertThatThrownBy(() -> orderCase.updatePayment(pedido))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Pedido não encontrado");
        }

        @Test
        void shouldHandleNotFoundException() {
            NotFoundException exception = new NotFoundException("Pedido não encontrado");

            // Verificação dos detalhes da resposta
            assertEquals(exception.getMessage(),"Pedido não encontrado");
        }
        @Test
        void shouldStoreErrorMessageInException() {
            String errorMessage = "Pedido não encontrado";

            // Criação da exceção
            NotFoundException exception = new NotFoundException(errorMessage);

            // Verificando se a mensagem de erro está sendo armazenada corretamente na exceção
            assertEquals(errorMessage, exception.getMessage());
        }
    }
}