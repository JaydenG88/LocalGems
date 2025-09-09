package com.localgems.localgems_backend.dto.responseDTO;

public class UserResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }   
    public void setUserId(Long userId) {
        this.userId = userId;
    }   
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

}
