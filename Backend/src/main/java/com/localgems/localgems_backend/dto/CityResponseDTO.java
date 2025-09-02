package com.localgems.localgems_backend.dto;

public class CityResponseDTO {
    private Long cityId;
    private String name;
    private String state;

    // Getters and Setters
    public Long getCityId() {
        return cityId;
    }
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

}
