package com.university.registration.model;

public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String username;
    private String password;
    private SessionState sessionState;

    protected Person(String id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.sessionState = SessionState.LOGGED_OUT;
    }

    public boolean login(String username, String password) {
        boolean success = this.username.equals(username) && this.password.equals(password);
        this.sessionState = success ? SessionState.LOGGED_IN : SessionState.LOGGED_OUT;
        return success;
    }

    public void logout() {
        this.sessionState = SessionState.LOGGED_OUT;
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
        return sessionState == SessionState.LOGGED_IN;
    }

    public SessionState getSessionState() {
        return sessionState;
    }
}
