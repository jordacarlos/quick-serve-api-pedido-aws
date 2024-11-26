package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class OrderPostEntity {
    private Long id;
    private Long customerID;
    private OrderStatusEnum status;
    private OrderPaymentStatusEnum paymentStatus;
    private List<OrderDTO> orderItems;
    private Double totalOrderValue;

    // Construtor sem argumentos
    public OrderPostEntity() {
    }

    public OrderPostEntity(Long id, Long customerID, OrderStatusEnum status, OrderPaymentStatusEnum paymentStatus, List<OrderDTO> orderItems, Double totalOrderValue) {
        this.id = id;
        this.customerID = customerID;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.orderItems = orderItems;
        this.totalOrderValue = totalOrderValue;
    }

    // Outros construtores...
    public OrderPostEntity(Long id, Long customerID, OrderPaymentStatusEnum paymentStatus, List<OrderDTO> orderItems, Double totalOrderValue) {
        this.id = id;
        this.customerID = customerID;
        this.paymentStatus = paymentStatus;
        this.orderItems = orderItems;
        this.totalOrderValue = totalOrderValue;
    }

    public OrderPostEntity(Long customerID, OrderStatusEnum status, OrderPaymentStatusEnum paymentStatus, List<OrderDTO> orderItems) {
        this.customerID = customerID;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.orderItems = orderItems;
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

    public List<OrderDTO> getOrderDTOs() {
        return this.orderItems;
    }

    public void setOrderDTOs(List<OrderDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public Double getTotalOrderValue() {
        return this.totalOrderValue;
    }

    public void setTotalOrderValue(Double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }
    @Override
    public String toString() {
        return "OrderPostEntity{" +
                "id=" + id +
                ", customerID=" + customerID +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", orderItems=" + orderItems +
                ", totalOrderValue=" + totalOrderValue +
                '}';
    }
}

