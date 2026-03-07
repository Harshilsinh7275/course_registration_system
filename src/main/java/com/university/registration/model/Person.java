package com.university.registration.model;

public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private boolean loggedIn;

    protected Person(String id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public boolean login(String username, String password) {
        boolean success = this.username.equals(username) && this.password.equals(password);
        this.loggedIn = success;
        return success;
    }

    public void logout() {
        this.loggedIn = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}

