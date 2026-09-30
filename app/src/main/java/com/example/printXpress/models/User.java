package com.example.printXpress.models;

public class User {
    public int id;
    public String name, email, phone, role;

    public User(int id, String name, String email, String phone, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
}