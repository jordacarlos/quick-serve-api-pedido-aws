package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

public class PaymentStatusDTO {
    private Long orderId;
    private String paymentStatus;


    public PaymentStatusDTO(Long orderId, String paymentStatus) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
    }

    // Getters e setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
