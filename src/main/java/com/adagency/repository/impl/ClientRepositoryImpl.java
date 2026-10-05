package com.adagency.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.adagency.model.Client;
import com.adagency.model.ClientStatus;
import com.adagency.repository.ClientRepository;
import com.adagency.util.DatabaseManager;

public class ClientRepositoryImpl implements ClientRepository {

    @Override
    public Client save(Client c) throws SQLException {
        String sql = "INSERT INTO clients(full_name, email, phone, company, status) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getCompany());
            ps.setString(5, c.getStatus() == null ? ClientStatus.ACTIVE.name() : c.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
        return c;
    }

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        String sql = "SELECT * FROM clients WHERE id = ?";
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
    public List<Client> findAll() throws SQLException {
        String sql = "SELECT * FROM clients ORDER BY id";
        List<Client> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public boolean update(Client c) throws SQLException {
        String sql = "UPDATE clients SET full_name=?, email=?, phone=?, company=?, status=? WHERE id=?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getCompany());
            ps.setString(5, c.getStatus().name());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Client> searchByName(String query) throws SQLException {
        return searchLike("SELECT * FROM clients WHERE LOWER(full_name) LIKE LOWER(?) ORDER BY full_name", query);
    }

    @Override
    public List<Client> searchByEmail(String query) throws SQLException {
        return searchLike("SELECT * FROM clients WHERE LOWER(email) LIKE LOWER(?) ORDER BY email", query);
    }

    @Override
    public List<Client> findByCompany(String company) throws SQLException {
        return searchLike("SELECT * FROM clients WHERE LOWER(company) LIKE LOWER(?) ORDER BY full_name", company);
    }

    @Override
    public List<Client> findByStatus(ClientStatus status) throws SQLException {
        String sql = "SELECT * FROM clients WHERE status = ? ORDER BY id";
        List<Client> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Client> findAllSortedByName(boolean desc) throws SQLException {
        String sql = "SELECT * FROM clients ORDER BY full_name " + (desc ? "DESC" : "ASC");
        return simpleQuery(sql);
    }

    @Override
    public List<Client> findAllSortedByCreatedAt(boolean desc) throws SQLException {
        String sql = "SELECT * FROM clients ORDER BY created_at " + (desc ? "DESC" : "ASC");
        return simpleQuery(sql);
    }

    @Override
    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM clients WHERE id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Client> searchLike(String sql, String param) throws SQLException {
        List<Client> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + param + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private List<Client> simpleQuery(String sql) throws SQLException {
        List<Client> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private Client map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        String statusStr = rs.getString("status");
        return new Client(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("company"),
                statusStr == null ? ClientStatus.ACTIVE : ClientStatus.valueOf(statusStr),
                ts == null ? null : ts.toLocalDateTime()
        );
    }
}