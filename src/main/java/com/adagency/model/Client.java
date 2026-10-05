package com.adagency.model;

import java.time.LocalDateTime;

public class Client {
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String company;
    private ClientStatus status;
    private LocalDateTime createdAt;

    public Client() {}

    public Client(String fullName, String email, String phone, String company) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = ClientStatus.ACTIVE;
    }

    public Client(int id, String fullName, String email, String phone,
                  String company, ClientStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public ClientStatus getStatus() { return status; }
    public void setStatus(ClientStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s | %s",
                id, fullName, email, phone,
                company == null ? "-" : company,
                status);
    }
}