package com.example.cs218marketmanager.data.model;

public class Manager {
    private String name;
    private String email;
    // Constructor, getters and setters for managers
    public Manager(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
