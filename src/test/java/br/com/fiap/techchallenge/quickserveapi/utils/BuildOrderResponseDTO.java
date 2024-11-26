package br.com.fiap.techchallenge.quickserveapi.utils;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;

import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.APROVADO;
import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderStatusEnum.RECEBIDO;

public abstract class BuildOrderResponseDTO {

    public static OrderResponseDTO buildOrderDTO() {
        // Criação de um item de pedido
        OrderItemResponseDTO itemResponse = new OrderItemResponseDTO(
                1L, // id
                "Produto Teste", // name
                "Categoria Teste", // category
                100.0, // price
                "Descrição do produto", // description
                "caminho/da/imagem.jpg" // imagePath
        );

        // Criando a lista de itens do pedido
        List<OrderItemResponseDTO> orderItems = List.of(itemResponse);

        // Criando a instância de OrderResponseDTO com os valores
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                1L, // id
                "1", // customerID
                OrderStatusEnum.RECEBIDO, // status
                OrderPaymentStatusEnum.APROVADO, // paymentStatus
                orderItems, // Lista de itens
                250.0 // totalOrderValue
        );

        return orderResponseDTO;
    }
}