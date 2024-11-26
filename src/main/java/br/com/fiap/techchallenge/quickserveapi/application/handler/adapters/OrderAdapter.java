package br.com.fiap.techchallenge.quickserveapi.application.handler.adapters;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.http.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderAdapter {

    @Autowired
    private ProductClient productClient; // Injeção de dependência do ProductClient

    public static OrderEntity mapSingleToOrderEntity(Map<String, Object> row) {
        if (row == null) {
            throw new RuntimeException("Ordem não encontrada");
        }

        // Conversão dos valores de 'status' e 'payment_status' para os enums respectivos
        OrderStatusEnum status = OrderStatusEnum.valueOf((String) row.get("status"));
        OrderPaymentStatusEnum paymentStatus = OrderPaymentStatusEnum.valueOf((String) row.get("payment_status"));

        // Conversão de 'order_id' e 'customer_id' de forma robusta
        Long orderId = parseLong(row.get("order_id"));
        Long customerId = parseLong(row.get("customer_id"));

        // Aqui, 'orderItems' será uma lista de OrderItem, por enquanto, colocamos null
        List<OrderItem> orderItems = null;

        // Criar a OrderEntity com os valores mapeados
        OrderEntity orderEntity = new OrderEntity(
                orderId,
                customerId,
                status,
                paymentStatus,
                orderItems,  // Lista de itens do pedido (atualmente null)
                (Double) row.get("total_order_value")
        );

        return orderEntity;
    }

    // Método de parse para garantir que a conversão de tipo seja feita de forma segura
    public static Long parseLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();  // Converte valores numéricos diretamente para Long
        }
        try {
            return Long.parseLong(value.toString());  // Tenta converter de String para Long
        } catch (NumberFormatException e) {
            return null;  // Retorna null se não puder converter
        }
    }

    public static List<OrderEntity> mapToOrderEntityList(List<Map<String, Object>> results) {
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Order não encontrada");
        }
        List<OrderEntity> orders = new ArrayList<>();
        for (Map<String, Object> row : results) {
            orders.add(mapSingleToOrderEntity(row)); // Não precisa de instância, o método pode ser estático
        }
        return orders;
    }


    public static OrderEntity mapToOrderEntity(Map<String, Object> data) {
        OrderEntity order = new OrderEntity();

        // Aqui você já pode tratar como String diretamente

        order.setId((Long) data.get("order_id"));

        // Conversão de customer_id de String para Long
        String customerIdString = (String) data.get("customer_id");
        Long customerId = Long.valueOf(customerIdString);  // Converte String para Long
        order.setCustomerID(customerId);

        // Convert status from String to OrderStatusEnum
        String statusString = (String) data.get("status");
        OrderStatusEnum status = OrderStatusEnum.valueOf(statusString);  // Correct conversion
        order.setStatus(status);

        order.setTotalOrderValue((Double) data.get("total_order_value"));
        order.setPaymentStatus(OrderPaymentStatusEnum.valueOf((String) data.get("payment_status")));

        return order;
    }

    public static OrderItem mapToOrderItem(Map<String, Object> data) {
        OrderItem item = new OrderItem();

        // Verifique se o valor para 'product_id' é null ou se não pode ser convertido para Long
        if (data.get("product_id") != null) {
            item.setProductId((Long) data.get("product_id"));
        }

        // Verifique se o valor para 'quantity' é null ou se não pode ser convertido para Integer
        if (data.get("quantity") != null) {
            item.setQuantity((Integer) data.get("quantity"));
        } else {
            // Tratar o caso quando quantity for null, por exemplo, definindo um valor padrão
            item.setQuantity(0);  // Valor padrão para quantidade
        }

        // Verifique se o valor para 'price_at_purchase' é null ou se não pode ser convertido para Double
        if (data.get("price_at_purchase") != null) {
            item.setPriceAtPurchase((Double) data.get("price_at_purchase"));
        }

        // Verifique se o valor para 'name' é null
        if (data.get("name") != null) {
            item.setName((String) data.get("name"));
        }

        return item;
    }
}