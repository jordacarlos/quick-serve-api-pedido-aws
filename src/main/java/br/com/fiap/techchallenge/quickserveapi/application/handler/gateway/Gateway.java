package br.com.fiap.techchallenge.quickserveapi.application.handler.gateway;

import br.com.fiap.techchallenge.quickserveapi.application.handler.adapters.OrderAdapter;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.external.DatabaseConnection;
import br.com.fiap.techchallenge.quickserveapi.application.handler.interfaces.ParametroBd;

import java.util.*;
import java.util.stream.Collectors;

public class Gateway {

    private final DatabaseConnection database;

    public Gateway(DatabaseConnection database) {
        this.database = database;
    }

    public Long saveOrder(OrderPostEntity orderEntity) {
        String[] campos = {"status", "customer_id", "payment_status", "total_order_value"};
        ParametroBd[] parametros = new ParametroBd[]{
                new ParametroBd("status", orderEntity.getStatus().toString()),
                new ParametroBd("customer_id", orderEntity.getCustomerID()),
                new ParametroBd("payment_status", orderEntity.getPaymentStatus().toString()),
                new ParametroBd("total_order_value", orderEntity.getTotalOrderValue())
        };
        List<Map<String, Object>> result = database.Inserir("orders", campos, parametros);
        return result != null && !result.isEmpty() ? (Long) result.get(0).get("id") : null;
    }

    public void saveOrderProduct(Long orderId, Long productId, long quantity) {
        String[] campos = {"order_id", "product_id", "product_quantity"};
        ParametroBd[] parametros = new ParametroBd[]{
                new ParametroBd("order_id", orderId),
                new ParametroBd("product_id", productId),
                new ParametroBd("product_quantity", quantity)
        };

        database.Inserir("order_products", campos, parametros);
    }

    public OrderEntity findOrderById(Long orderId) {
        // Definindo os campos que queremos buscar na tabela 'orders'
        String[] campos = {"order_id", "status", "customer_id", "total_order_value", "payment_status"};

        // Criando o parâmetro para a consulta (filtrando pelo 'order_id')
        ParametroBd[] parametros = {new ParametroBd("order_id", orderId)};

        // Buscando o pedido no banco de dados
        List<Map<String, Object>> result = database.buscarPorParametros("orders", campos, parametros);
        // Verificando se algum pedido foi encontrado
        if (result == null || result.isEmpty()) {
            return null; // Retorna null se não encontrar nenhum pedido com o ID fornecido
        }

        // Mapeando os dados da consulta para a entidade OrderEntity
        OrderEntity orderEntity = OrderAdapter.mapToOrderEntity(result.get(0)); // Mapeia a primeira linha da consulta

        // Agora, podemos carregar os itens do pedido (caso haja)
        List<OrderItem> orderItems = findOrderItemsByOrderId(orderId);
        orderEntity.setOrderItems(orderItems);

        return orderEntity; // Retorna o pedido com os itens preenchidos
    }

    private List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        // Definindo os campos da tabela 'order_items'
        String[] campos = {"product_id", "product_quantity"};

        // Criando o parâmetro para buscar os itens do pedido
        ParametroBd[] parametros = {new ParametroBd("order_id", orderId)};

        // Buscando os itens associados ao pedido
        List<Map<String, Object>> itemsResult = database.buscarPorParametros("order_products", campos, parametros);

        // Mapeando os itens encontrados para objetos OrderItem
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemData : itemsResult) {
            // Mapeando cada item para a entidade OrderItem
            OrderItem item = OrderAdapter.mapToOrderItem(itemData);
            orderItems.add(item);
        }

        return orderItems; // Retorna a lista de itens do pedido
    }

    public List<OrderEntity> findAllOrders() {
        // Definindo os campos que queremos buscar na tabela 'orders'
        String[] campos = {"order_id", "status", "customer_id", "total_order_value", "payment_status"};

        ParametroBd[] parametros = {};
        // Buscando todos os pedidos na base de dados
        List<Map<String, Object>> result = database.buscarPorParametros("orders", campos,parametros);

        // Verificando se algum pedido foi encontrado
        if (result == null || result.isEmpty()) {
            return Collections.emptyList(); // Retorna uma lista vazia se não houver pedidos
        }

        // Mapeando os dados da consulta para entidades OrderEntity
        List<OrderEntity> orders = result.stream()
                .map(orderData -> {
                    OrderEntity orderEntity = OrderAdapter.mapToOrderEntity(orderData);
                    // Carrega os itens de cada pedido
                    List<OrderItem> orderItems = findOrderItemsByOrderId(orderEntity.getId());
                    orderEntity.setOrderItems(orderItems);
                    return orderEntity;
                })
                .collect(Collectors.toList());

        return orders;
    }

    public OrderEntity updateOrderStatus(Long orderId, OrderStatusEnum status) {
        String[] campos = {"status"};
        ParametroBd[] parametros = {
                new ParametroBd("status", status.toString()),
                new ParametroBd("order_id", orderId)
        };

        // Atualiza o status no banco de dados
        database.Update("orders", campos, parametros);

        return findOrderById(orderId);
    }

    public OrderEntity updatePaymentStatus(Long orderId, OrderPaymentStatusEnum status) {

        String[] campos = {"payment_status"};
        ParametroBd[] parametros = {
                new ParametroBd("payment_status", status.toString()),
                new ParametroBd("order_id", orderId)
        };

        // Atualiza o status no banco de dados
        database.Update("orders", campos, parametros);

        return findOrderById(orderId);
    }


    public PaymentStatusDTO findPaymentStatus(Long orderId) {
        String[] campos = {"order_id", "payment_status"};
        ParametroBd[] parametros = {new ParametroBd("order_id", orderId)};
        List<Map<String, Object>> result = database.buscarPorParametros("orders", campos, parametros);

        if (result != null && !result.isEmpty()) {
            Long orderIdFromDb = (Long) result.get(0).get("order_id");
            String paymentStatus = result.get(0).get("payment_status").toString();
            return new PaymentStatusDTO(orderIdFromDb, paymentStatus);
        }
        return null;
    }


    public List<OrderEntity> findOrdersWithSorting(Map<String, Integer> caseFiltros, String sortOrder) {
        // Definindo os campos que queremos buscar na tabela 'orders'
        String[] campos = {"order_id", "status", "customer_id", "total_order_value", "payment_status"};

        // Nenhum parâmetro adicional para filtros neste exemplo
        ParametroBd[] parametros = {new ParametroBd("status", "FINALIZADO")};

        // Nenhum filtro adicional para ordenação padrão
        String[] filtros = {};

        // Mapeamento de caso para a ordenação
        caseFiltros = new HashMap<>();
        caseFiltros.put("PRONTO", 1);
        caseFiltros.put("EM_PREPARACAO", 2);
        caseFiltros.put("RECEBIDO", 3);

        // Realizando a consulta com filtros e ordenação
        List<Map<String, Object>> resultados = database.buscarPorFiltros("orders", campos, parametros, filtros, caseFiltros,  "order_id ASC");

        List<OrderEntity> orders = OrderAdapter.mapToOrderEntityList(resultados);

        // Retornando a lista de pedidos com os itens já carregados
        orders.forEach(order -> {
            List<OrderItem> orderItems = findOrderItemsByOrderId(order.getId());
            order.setOrderItems(orderItems);
        });

        return orders; // Retorna os pedidos com os itens associados
    }

}
