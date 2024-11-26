package br.com.fiap.techchallenge.quickserveapi.utils;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.*;

import java.util.List;

import static br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPaymentStatusEnum.PENDENTE;

public abstract class BuildOrderPaymentStatusDTO {

    public static PaymentStatusDTO buildOrderStatusDTO() {
        // Criação de um item de pedido
        PaymentStatusDTO statusResponse = new PaymentStatusDTO(
                1L, // id
                "PENDENTE"
        );

        return statusResponse;
    }
}