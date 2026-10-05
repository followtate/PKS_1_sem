package com.adagency.repository.impl;

import com.adagency.model.Campaign;
import com.adagency.model.CampaignStatus;
import com.adagency.model.CampaignType;
import com.adagency.repository.CampaignRepository;
import com.adagency.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CampaignRepositoryImpl implements CampaignRepository {

    @Override
    public Campaign save(Campaign c) throws SQLException {
        String sql = "INSERT INTO campaigns(title, description, budget, status, type, " +
                "start_date, end_date, client_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, c);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
        return c;
    }

    @Override
    public Optional<Campaign> findById(int id) throws SQLException {
        String sql = "SELECT * FROM campaigns WHERE id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Campaign> findAll() throws SQLException {
        String sql = "SELECT * FROM campaigns ORDER BY id";
        List<Campaign> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public boolean update(Campaign c) throws SQLException {
        String sql = "UPDATE campaigns SET title=?, description=?, budget=?, status=?, " +
                "type=?, start_date=?, end_date=?, client_id=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fill(ps, c);
            ps.setInt(9, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM campaigns WHERE id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Campaign> searchByTitle(String query) throws SQLException {
        return queryList("SELECT * FROM campaigns WHERE LOWER(title) LIKE LOWER(?) ORDER BY title",
                "%" + query + "%");
    }

    @Override
    public List<Campaign> findByType(CampaignType type) throws SQLException {
        return queryList("SELECT * FROM campaigns WHERE type = ? ORDER BY id", type.name());
    }

    @Override
    public List<Campaign> findByStatus(CampaignStatus status) throws SQLException {
        return queryList("SELECT * FROM campaigns WHERE status = ? ORDER BY id", status.name());
    }

    @Override
    public List<Campaign> findByClientId(int clientId) throws SQLException {
        String sql = "SELECT * FROM campaigns WHERE client_id = ? ORDER BY id";
        List<Campaign> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Campaign> findByBudgetRange(BigDecimal min, BigDecimal max) throws SQLException {
        String sql = "SELECT * FROM campaigns WHERE budget BETWEEN ? AND ? ORDER BY budget";
        List<Campaign> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public int countByClientId(int clientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM campaigns WHERE client_id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private List<Campaign> queryList(String sql, String param) throws SQLException {
        List<Campaign> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private void fill(PreparedStatement ps, Campaign c) throws SQLException {
        ps.setString(1, c.getTitle());
        ps.setString(2, c.getDescription());
        ps.setBigDecimal(3, c.getBudget());
        ps.setString(4, c.getStatus().name());
        ps.setString(5, c.getType().name());
        ps.setDate(6, Date.valueOf(c.getStartDate()));
        ps.setDate(7, Date.valueOf(c.getEndDate()));
        ps.setInt(8, c.getClientId());
    }

    private Campaign map(ResultSet rs) throws SQLException {
        return new Campaign(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getBigDecimal("budget"),
                CampaignStatus.valueOf(rs.getString("status")),
                CampaignType.valueOf(rs.getString("type")),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getInt("client_id")
        );
    }
}