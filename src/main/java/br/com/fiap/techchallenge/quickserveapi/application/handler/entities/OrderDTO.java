package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

public class OrderDTO {
    private Long id;
    private Integer quantity; // Adicionando o campo de quantidade

    // Construtores, getters e setters

    public OrderDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity; // Getter para a quantidade
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity; // Setter para a quantidade
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
