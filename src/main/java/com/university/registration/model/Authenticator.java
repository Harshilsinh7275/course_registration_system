package com.university.registration.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Authenticator {
    private final List<Person> users = new ArrayList<>();

    public Authenticator() {
    }

    public Authenticator(List<Person> users) {
        if (users != null) {
            this.users.addAll(users);
        }
    }

    public Person login(String username, String password) {
        return users.stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .filter(user -> user.login(username, password))
                .findFirst()
                .orElse(null);
    }

    public void logout(Person user) {
        if (user != null) {
            user.logout();
        }
    }

    public List<Person> getUsers() {
        return users;
    }

    public void registerUser(Person user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        boolean duplicate = users.stream().anyMatch(existing ->
                existing.getId().equalsIgnoreCase(user.getId())
                        || existing.getEmail().equalsIgnoreCase(user.getEmail())
                        || existing.getUsername().equalsIgnoreCase(user.getUsername()));
        if (duplicate) {
            throw new IllegalStateException("Duplicate account detected (ID/email/username).");
        }
        users.add(user);
    }

    public boolean isRoleAuthorized(Person user, String role) {
        if (user == null || role == null) {
            return false;
        }
        return switch (role.toUpperCase()) {
            case "STUDENT" -> user instanceof Student;
            case "INSTRUCTOR" -> user instanceof Instructor;
            case "ADMIN" -> user instanceof Admin;
            default -> false;
        };
    }
}
