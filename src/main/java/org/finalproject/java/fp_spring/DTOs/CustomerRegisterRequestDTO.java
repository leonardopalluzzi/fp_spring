package org.finalproject.java.fp_spring.DTOs;

public class CustomerRegisterRequestDTO {

    private Integer userId;
    private String serviceCode;

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getServiceCode() {
        return this.serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

}
