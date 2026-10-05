package com.adagency.service;

import java.sql.SQLException;
import java.util.List;

import com.adagency.exception.BusinessException;
import com.adagency.exception.EntityNotFoundException;
import com.adagency.exception.ValidationException;
import com.adagency.model.Client;
import com.adagency.model.ClientStatus;
import com.adagency.repository.CampaignRepository;
import com.adagency.repository.ClientRepository;
import com.adagency.repository.impl.CampaignRepositoryImpl;
import com.adagency.repository.impl.ClientRepositoryImpl;

public class ClientService {
    private final ClientRepository clientRepo = new ClientRepositoryImpl();
    private final CampaignRepository campaignRepo = new CampaignRepositoryImpl();

    private void validate(Client c) {
        if (c.getFullName() == null || c.getFullName().isBlank())
            throw new ValidationException("ФИО клиента обязательно.");
        if (c.getEmail() == null || !c.getEmail().contains("@"))
            throw new ValidationException("Email некорректен.");
        if (c.getPhone() == null || c.getPhone().isBlank())
            throw new ValidationException("Телефон обязателен.");
        if (c.getStatus() == null)
            throw new ValidationException("Статус клиента обязателен.");
    }

    public Client create(Client c) {
        validate(c);
        try {
            return clientRepo.save(c);
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("unique"))
                throw new BusinessException("Клиент с таким email уже существует.");
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    public List<Client> findAll() {
        try { return clientRepo.findAll(); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public Client findById(int id) {
        try {
            return clientRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Клиент id=" + id + " не найден."));
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public void update(Client c) {
        validate(c);
        try {
            if (!clientRepo.update(c))
                throw new EntityNotFoundException("Клиент id=" + c.getId() + " не найден.");
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public void delete(int id) {
        try {
            int count = campaignRepo.countByClientId(id);
            if (count > 0)
                throw new BusinessException("Нельзя удалить клиента: у него " + count + " кампаний.");
            if (!clientRepo.delete(id))
                throw new EntityNotFoundException("Клиент id=" + id + " не найден.");
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> searchByName(String q) {
        try { return clientRepo.searchByName(q); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> searchByEmail(String q) {
        try { return clientRepo.searchByEmail(q); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> searchByCompany(String q) {
        try { return clientRepo.findByCompany(q); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> filterByStatus(ClientStatus status) {
        try { return clientRepo.findByStatus(status); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> sortByName(boolean desc) {
        try { return clientRepo.findAllSortedByName(desc); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Client> sortByCreatedAt(boolean desc) {
        try { return clientRepo.findAllSortedByCreatedAt(desc); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public int totalCount() {
        try { return clientRepo.findAll().size(); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }
}