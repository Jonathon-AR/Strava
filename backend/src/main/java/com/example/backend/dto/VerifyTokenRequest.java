package com.example.backend.dto;

public class VerifyTokenRequest {
    private String jstToken;

    public String getJstToken() {
        return jstToken;
    }

    public void setJstToken(String jstToken) {
        this.jstToken = jstToken;
    }
}