package com.example.printXpress.models;

public class Order {
    public int id, quantity;
    public String customerEmail, productName, instructions, deliveryType, status, designPath;
    public double totalPrice;

    public Order(int id, String customerEmail, String productName, int quantity, double totalPrice,
                 String instructions, String deliveryType, String status, String designPath) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.instructions = instructions;
        this.deliveryType = deliveryType;
        this.status = status;
        this.designPath = designPath;
    }
}
