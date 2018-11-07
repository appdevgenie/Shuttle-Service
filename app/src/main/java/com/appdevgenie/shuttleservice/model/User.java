package com.appdevgenie.shuttleservice.model;

public class User {

    private String name;
    private String email;
    private String contactNumber;

    public User() {
    }

    public User(String name, String email, String contactNumber) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
