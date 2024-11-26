package br.com.fiap.techchallenge.quickserveapi.adapters;
import br.com.fiap.techchallenge.quickserveapi.application.handler.adapters.OrderAdapter;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderEntity;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class OrderAdapterTest {

    @Autowired
    private OrderAdapter orderAdapter;

    @Test
    void deveMapearParaOrderEntityComSucesso() {
        Map<String, Object> row = new HashMap<>();
        row.put("order_id", 1L);
        row.put("customer_id", 123L);
        row.put("status", "RECEBIDO");
        row.put("payment_status", "PENDENTE");
        row.put("total_order_value", 250.75);

        OrderEntity result = OrderAdapter.mapSingleToOrderEntity(row);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(123L, result.getCustomerID());
    }
}

