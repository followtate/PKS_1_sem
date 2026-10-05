package com.adagency.repository;

import com.adagency.model.Campaign;
import com.adagency.model.CampaignStatus;
import com.adagency.model.CampaignType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
    Campaign save(Campaign c) throws SQLException;
    Optional<Campaign> findById(int id) throws SQLException;
    List<Campaign> findAll() throws SQLException;
    boolean update(Campaign c) throws SQLException;
    boolean delete(int id) throws SQLException;

    List<Campaign> searchByTitle(String query) throws SQLException;
    List<Campaign> findByType(CampaignType type) throws SQLException;
    List<Campaign> findByStatus(CampaignStatus status) throws SQLException;
    List<Campaign> findByClientId(int clientId) throws SQLException;
    List<Campaign> findByBudgetRange(BigDecimal min, BigDecimal max) throws SQLException;

    int countByClientId(int clientId) throws SQLException;
}