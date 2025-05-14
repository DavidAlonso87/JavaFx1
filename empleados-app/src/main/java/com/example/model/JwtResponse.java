package com.example.model;

public class JwtResponse {

    private String token;

    // Constructor
    public JwtResponse(String token) {
        this.token = token;
    }

    // Getter y setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
