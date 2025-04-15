package com.example.backend.dto;

public class AuthResponse {

    private String token;
    private String refreshToken;
    private boolean newUser;
    private String username;
    private String email;

    public AuthResponse(String token, String refreshToken, boolean newUser, String username, String email) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.newUser = newUser;
        this.username = username;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}