package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private String customerID;
    private String status;
    private String paymentStatus;
    private List<OrderItemResponseDTO> orderItems;
    private Double totalOrderValue;

    // Construtores
    public OrderResponseDTO(Long id, String customerID, OrderStatusEnum status, OrderPaymentStatusEnum paymentStatus,
                            List<OrderItemResponseDTO> orderItems, Double totalOrderValue) {
        this.id = id;
        this.customerID = customerID;
        this.status = String.valueOf(status);  // Convertendo o enum para string
        this.paymentStatus = String.valueOf(paymentStatus); // Convertendo o enum para string
        this.orderItems = orderItems;
        this.totalOrderValue = totalOrderValue;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<OrderItemResponseDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponseDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public Double getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(Double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }
}