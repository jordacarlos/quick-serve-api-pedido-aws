package br.com.fiap.techchallenge.quickserveapi.application.handler.usecases;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;
import br.com.fiap.techchallenge.quickserveapi.application.handler.exception.NotFoundException;
import br.com.fiap.techchallenge.quickserveapi.application.handler.gateway.Gateway;
import br.com.fiap.techchallenge.quickserveapi.application.handler.http.ProductClient;

import java.util.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderCase {

    private final Gateway gateway;
    private final ProductClient productClient;

    public OrderCase(Gateway gateway,ProductClient productClient) {
        this.gateway = gateway;
        this.productClient = productClient;
    }

    public OrderResponseDTO save(OrderPostEntity orderEntity) {
        // Pega a lista de OrderDTOs, não um único OrderDTO
        List<OrderDTO> orderDTOs = orderEntity.getOrderDTOs();

        OrderDTO produto = orderDTOs.get(0); // Aqui você pega o primeiro item da lista.
        // Obtém o produto correspondente através do cliente
        ProductDTO product = productClient.getProductById(produto.getId());
        // Calcula o valor do pedido com base no preço do produto e quantidade do pedido
        Double valorPedido = product.getPrice() * produto.getQuantity();

        // Define o valor total do pedido no orderEntity
        orderEntity.setTotalOrderValue(valorPedido);

        // Salva o pedido e obtém o ID do pedido gerado
        Long orderId = gateway.saveOrder(orderEntity);
        orderEntity.setId(orderId);

        // Salva cada item do pedido na base de dados
        orderDTOs.forEach(item -> {
            gateway.saveOrderProduct(orderId, item.getId(), item.getQuantity());
        });
        return findById(orderId);
    }

    public OrderResponseDTO findById(Long id) {
        // Recupera o pedido do gateway
        OrderEntity orderEntity = gateway.findOrderById(id);
        if (orderEntity == null) {
            // Lança a exceção NotFoundException
            throw new NotFoundException("Pedido não encontrado");
        }
            // Criação do DTO de resposta
            List<OrderItemResponseDTO> orderItemResponseDTOs = orderEntity.getOrderItems().stream()
                    .map(item -> {
                        ProductDTO product = productClient.getProductById(item.getProductId());
                        if (product != null) {
                            return new OrderItemResponseDTO(
                                    item.getProductId(),
                                    product.getName(),
                                    product.getCategory(),
                                    product.getPrice(),
                                    product.getDescription(),
                                    product.getImagePath()
                            );
                        }
                        return null;
                    })
                    .collect(Collectors.toList());

            // Retorno do DTO preenchido
            return new OrderResponseDTO(
                    orderEntity.getId(),
                    String.valueOf(orderEntity.getCustomerID()),
                    orderEntity.getStatus(),
                    orderEntity.getPaymentStatus(),
                    orderItemResponseDTOs,
                    orderEntity.getTotalOrderValue()
            );
    }

    public PaymentStatusDTO checkPaymentStatus(Long id) {
        // Recupera o pedido do gateway
        PaymentStatusDTO pagamento = gateway.findPaymentStatus(id);

        if (pagamento == null) {
            throw new NotFoundException("Pedido não encontrado");
        }
        return pagamento;
    }

    public List<OrderResponseDTO> findAll() {
        // Recupera todos os pedidos
        List<OrderEntity> orderEntities = gateway.findAllOrders();
        List<OrderResponseDTO> orderResponseDTOs = new ArrayList<>();

        for (OrderEntity orderEntity : orderEntities) {
            // Para cada pedido, cria os itens de resposta com os produtos
            List<OrderItemResponseDTO> orderItemResponseDTOs = orderEntity.getOrderItems().stream()
                    .map(item -> {
                        // Consultando o produto para cada item do pedido
                        ProductDTO product = productClient.getProductById(item.getProductId());
                        if (product != null) {
                            return new OrderItemResponseDTO(
                                    item.getProductId(),
                                    product.getName(),
                                    product.getCategory(),
                                    product.getPrice(),
                                    product.getDescription(),
                                    product.getImagePath()
                            );
                        }
                        return null;
                    })
                    .collect(Collectors.toList());

            // Adiciona o DTO do pedido à lista de respostas
            orderResponseDTOs.add(new OrderResponseDTO(
                    orderEntity.getId(),
                    String.valueOf(orderEntity.getCustomerID()),
                    orderEntity.getStatus(),
                    orderEntity.getPaymentStatus(),
                    orderItemResponseDTOs,
                    orderEntity.getTotalOrderValue()
            ));
        }

        return orderResponseDTOs; // Retorna a lista de pedidos com os produtos
    }

    public List<OrderResponseDTO> listByFiltersWithSorting(String sortOrder) {
        // Mapeamento de caso para a ordenação
        Map<String, Integer> caseFiltros = new HashMap<>();
        caseFiltros.put("PRONTO", 1);
        caseFiltros.put("EM_PREPARACAO", 2);
        caseFiltros.put("RECEBIDO", 3);

        // Chama o Gateway para buscar os pedidos com a ordenação e filtros
        List<OrderEntity> orderEntities = gateway.findOrdersWithSorting(caseFiltros, sortOrder);

        // Mapeia os resultados para DTOs de pedido e enriquece com os dados dos produtos
        return orderEntities.stream()
                .map(orderEntity -> {
                    // Enriquecer os itens do pedido com os dados dos produtos
                    List<OrderItemResponseDTO> orderItemResponseDTOs = orderEntity.getOrderItems().stream()
                            .map(item -> {
                                // Enriquecer os dados de produto usando o client
                                ProductDTO product = productClient.getProductById(item.getProductId());
                                if (product != null) {
                                    return new OrderItemResponseDTO(
                                            item.getProductId(),
                                            product.getName(),
                                            product.getCategory(),
                                            product.getPrice(),
                                            product.getDescription(),
                                            product.getImagePath()
                                    );
                                }
                                return null;
                            })
                            .collect(Collectors.toList());
                    // Retorna o DTO do pedido enriquecido
                    return new OrderResponseDTO(
                            orderEntity.getId(),
                            String.valueOf(orderEntity.getCustomerID()),
                            orderEntity.getStatus(),
                            orderEntity.getPaymentStatus(),
                            orderItemResponseDTOs,
                            orderEntity.getTotalOrderValue()
                    );
                })
                .collect(Collectors.toList());
    }

    public OrderResponseDTO updateStatus(OrderResponseDTO order) {
        // Tenta atualizar o status do pedido, pode retornar null se o pedido não for encontrado
        OrderEntity orderEntity = gateway.updateOrderStatus(order.getId(), OrderStatusEnum.valueOf(order.getStatus()));

        if (orderEntity == null) {
            // Se o pedido não for encontrado, lança a exceção
            throw new NotFoundException("Pedido não encontrado");
        }

        // Caso o pedido seja encontrado, mapeia os itens do pedido
        List<OrderItemResponseDTO> orderItemResponseDTOs = orderEntity.getOrderItems().stream()
                .map(item -> {
                    ProductDTO product = productClient.getProductById(item.getProductId());
                    if (product != null) {
                        return new OrderItemResponseDTO(
                                item.getProductId(),
                                product.getName(),
                                product.getCategory(),
                                product.getPrice(),
                                product.getDescription(),
                                product.getImagePath()
                        );
                    }
                    return null;
                })
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                orderEntity.getId(),
                String.valueOf(orderEntity.getCustomerID()),
                orderEntity.getStatus(),
                orderEntity.getPaymentStatus(),
                orderItemResponseDTOs,
                orderEntity.getTotalOrderValue()
        );
    }

    public OrderResponseDTO updatePayment(OrderResponseDTO order) {
        // Tenta atualizar o status de pagamento do pedido, pode retornar null se o pedido não for encontrado
        OrderEntity orderEntity = gateway.updatePaymentStatus(order.getId(), OrderPaymentStatusEnum.valueOf(order.getPaymentStatus()));

        if (orderEntity == null) {
            // Se o pedido não for encontrado, lança a exceção
            throw new NotFoundException("Pedido não encontrado");
        }

        // Caso o pedido seja encontrado, mapeia os itens do pedido
        List<OrderItemResponseDTO> orderItemResponseDTOs = orderEntity.getOrderItems().stream()
                .map(item -> {
                    ProductDTO product = productClient.getProductById(item.getProductId());
                    if (product != null) {
                        return new OrderItemResponseDTO(
                                item.getProductId(),
                                product.getName(),
                                product.getCategory(),
                                product.getPrice(),
                                product.getDescription(),
                                product.getImagePath()
                        );
                    } else {
                        return new OrderItemResponseDTO(
                                item.getProductId(),
                                "Produto não encontrado",
                                "N/A",
                                0.0,
                                "Sem descrição",
                                "Sem imagem"
                        );
                    }
                })
                .collect(Collectors.toList());

        return new OrderResponseDTO(
                orderEntity.getId(),
                String.valueOf(orderEntity.getCustomerID()),
                orderEntity.getStatus(),
                orderEntity.getPaymentStatus(),
                orderItemResponseDTOs,
                orderEntity.getTotalOrderValue()
        );
    }

}