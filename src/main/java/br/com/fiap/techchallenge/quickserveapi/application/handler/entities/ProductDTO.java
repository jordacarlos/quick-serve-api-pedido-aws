package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity; // Adicionando o campo de quantidade
    private String category;
    private String description;
    private String imagePath;


    // Construtores, getters e setters

    public ProductDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity; // Getter para a quantidade
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity; // Setter para a quantidade
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';

    }
}