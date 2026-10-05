package com.adagency.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.adagency.model.Client;
import com.adagency.model.ClientStatus;

public interface ClientRepository {
    Client save(Client c) throws SQLException;
    Optional<Client> findById(int id) throws SQLException;
    List<Client> findAll() throws SQLException;
    boolean update(Client c) throws SQLException;
    boolean delete(int id) throws SQLException;

    List<Client> searchByName(String query) throws SQLException;
    List<Client> searchByEmail(String query) throws SQLException;
    List<Client> findByCompany(String company) throws SQLException;

    List<Client> findByStatus(ClientStatus status) throws SQLException;
    List<Client> findAllSortedByName(boolean desc) throws SQLException;
    List<Client> findAllSortedByCreatedAt(boolean desc) throws SQLException;

    boolean existsById(int id) throws SQLException;
}