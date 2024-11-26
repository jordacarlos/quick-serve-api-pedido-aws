package br.com.fiap.techchallenge.quickserveapi.application.handler.controllers;

import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderPostEntity;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.OrderResponseDTO;
import br.com.fiap.techchallenge.quickserveapi.application.handler.entities.PaymentStatusDTO;
import br.com.fiap.techchallenge.quickserveapi.application.handler.usecases.OrderCase;

import java.util.List;

public class OrderController {

    private final OrderCase orderCase;

    public OrderController(OrderCase orderCase) {
        this.orderCase = orderCase;
    }

    public OrderResponseDTO save(OrderPostEntity orderInput){
       return orderCase.save(orderInput);
    }

    public OrderResponseDTO findById(Long id){
        return orderCase.findById(id);
    }

    public PaymentStatusDTO checkPaymentStatus(Long id) {
        return orderCase.checkPaymentStatus(id);
    }

    public List<OrderResponseDTO> findAll() {
        return orderCase.findAll();
    }

    public List<OrderResponseDTO> findAllSorted(String sortOrder) {
        return orderCase.listByFiltersWithSorting(sortOrder);
    }

    public OrderResponseDTO updateStatus(OrderResponseDTO order) {
        return orderCase.updateStatus(order);
    }

    public OrderResponseDTO paymentApprover(OrderResponseDTO order) {
        return orderCase.updatePayment(order);
    }
}