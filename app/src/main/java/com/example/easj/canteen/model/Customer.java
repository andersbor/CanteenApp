package com.example.easj.canteen.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private int id;
    private String firstname, lastname, email, password, pictureUrl;

    private String message; // error messages from AsynTasks, like LoginTask

    public Customer() { /* no-args constructor needed for serialization */}

    public Customer(String message) {
        this.message = message;
    }

    public Customer(int id, String firstname, String lastname, String email, String password, String pictureUrl) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.pictureUrl = pictureUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname + " " + email;
    }
}
