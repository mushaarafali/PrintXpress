package com.example.printXpress.models;

public class Product {
    public int id;
    public String name, category, material, size, description, imagePath;
    public double price;

    public Product(int id, String name, String category, String material, String size, double price, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.material = material;
        this.size = size;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
    }
}
