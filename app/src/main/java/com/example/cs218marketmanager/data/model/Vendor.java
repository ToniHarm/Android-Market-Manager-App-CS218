package com.example.cs218marketmanager.data.model;

import java.util.List;

public class Vendor {
    private long id; // Vendor ID
    private long userId; // User ID
    private String firstName; // First name of the user
    private String lastName; // Last name of the user
    private String email; // Email of the user
    private List<String> products; // List of products offered by the vendor
    private String stallNumber; // Stall number

    public Vendor() {

    }
    // Constructor
    public Vendor(long id, long userId, String firstName, String lastName, String email, List<String> products, String stallNumber) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.products = products;
        this.stallNumber = stallNumber;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getStallNumber() {
        return stallNumber;
    }

    public void setStallNumber(String stallNumber) {
        this.stallNumber = stallNumber;
    }
}
