package com.example.cs218marketmanager.data.model;

import java.util.List;

public class VendorApplication {
    private long id;                // ID of the vendor application (unique for each application)
    private User user;                    // User object containing user details
    private long userId;
    private List<String> products;   // List of product types applied by the vendor
    private String status;           // Status of the application (e.g., pending, approved, rejected)

    public VendorApplication(long id, long userId, User user, List<String> products, String status) {
        this.id = id;
        this.user = user;
        this.userId = userId;
        this.products = products;
        this.status = status;
    }

    // Getter and Setter methods
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {           // Getter for user
        return user;
    }

    public void setUser(User user) {  // Setter for user
        this.user = user;
    }

    public long getUserId() {           // Getter for userId
        return userId;
    }

    public void setUserId(long userId) {  // Setter for userId
        this.userId = userId;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
