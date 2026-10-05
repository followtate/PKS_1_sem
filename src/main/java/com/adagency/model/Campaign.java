package com.adagency.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Campaign {
    private int id;
    private String title;
    private String description;
    private BigDecimal budget;
    private CampaignStatus status;
    private CampaignType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private int clientId;

    public Campaign() {}

    public Campaign(String title, String description, BigDecimal budget,
                    CampaignStatus status, CampaignType type,
                    LocalDate startDate, LocalDate endDate, int clientId) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.status = status;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clientId = clientId;
    }

    public Campaign(int id, String title, String description, BigDecimal budget,
                    CampaignStatus status, CampaignType type,
                    LocalDate startDate, LocalDate endDate, int clientId) {
        this(title, description, budget, status, type, startDate, endDate, clientId);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public CampaignStatus getStatus() { return status; }
    public void setStatus(CampaignStatus status) { this.status = status; }
    public CampaignType getType() { return type; }
    public void setType(CampaignType type) { this.type = type; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s..%s | clientId=%d | %s ₽",
                id, title, type, status, startDate, endDate, clientId, budget);
    }
}