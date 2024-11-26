package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

import java.util.List;
import java.util.UUID;

public class OrderEntity {
    private Long id;
    private Long customerID;
    private OrderStatusEnum status;
    private OrderPaymentStatusEnum paymentStatus;
    private List<OrderItem> orderItems;
    private Double totalOrderValue;

    // Construtor sem argumentos
    public OrderEntity() {
    }

    public OrderEntity(Long id, Long customerID, OrderStatusEnum status, OrderPaymentStatusEnum paymentStatus, List<OrderItem> orderItems, Double totalOrderValue) {
        this.id = id;
        this.customerID = customerID;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.orderItems = orderItems;
        this.totalOrderValue = totalOrderValue;
    }
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerID() {
        return this.customerID;
    }

    public void setCustomerID(Long  customerID) {
        this.customerID = customerID;
    }

    public OrderStatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public OrderPaymentStatusEnum getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(OrderPaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<OrderItem> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Double getTotalOrderValue() {
        if (this.totalOrderValue == null) {
            return calculateTotalOrderValue();
        }
        return this.totalOrderValue;
    }

    public void setTotalOrderValue(Double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }

    private Double calculateTotalOrderValue() {
        return orderItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }
}

