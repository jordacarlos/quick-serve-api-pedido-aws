package br.com.fiap.techchallenge.quickserveapi.service;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import br.com.fiap.techchallenge.quickserveapi.application.handler.gateway.Gateway;
import br.com.fiap.techchallenge.quickserveapi.application.handler.http.ProductClient;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;
import br.com.fiap.techchallenge.quickserveapi.utils.BuildOrderHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.PENDENTE;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/data.sql")
public class OrderServiceTest {

    @Mock
    private Gateway gateway;
    private AutoCloseable mock;

    @Mock
    private ProductClient productClient; // Certifique-se de que o ProductClient é mockado

    @InjectMocks
    private OrderCase orderCase; // A classe de serviço onde você usa o productClient

    @BeforeEach
    void setup() {
        // Inicializa os mocks
        mock = MockitoAnnotations.openMocks(this);

        // Simula um produto com todos os campos preenchidos
        ProductDTO mockProduct = new ProductDTO();
        mockProduct.setId(1L);
        mockProduct.setName("x-TUDO");
        mockProduct.setPrice(125.0);
        mockProduct.setQuantity(100);
        mockProduct.setCategory("LANCHE");
        mockProduct.setDescription("Descrição do produto de teste");
        mockProduct.setImagePath("/images/produto-teste.jpg");

        // Configura o mock para retornar este produto ao chamar getProductById
        when(productClient.getProductById(1L)).thenReturn(mockProduct);
    }

    @AfterEach
    void teardown() throws Exception {
        // Fecha os mocks após cada teste
        mock.close();
    }
    @Test
    void deveLancarExcecaoQuandoNaoEncontrarPagamento() {
        // Mockando o gateway para retornar null
        Long id = 1L;
        when(gateway.findPaymentStatus(id)).thenReturn(null);

        // Verifica se a exceção é lançada
        assertThrows(NotFoundException.class, () -> orderCase.checkPaymentStatus(id));

        // Verifica se o gateway foi chamado corretamente
        verify(gateway, never()).findPaymentStatus(id);
    }
}