package com.adagency.service;

import com.adagency.exception.BusinessException;
import com.adagency.exception.EntityNotFoundException;
import com.adagency.exception.ValidationException;
import com.adagency.model.*;
import com.adagency.repository.CampaignRepository;
import com.adagency.repository.ClientRepository;
import com.adagency.repository.impl.CampaignRepositoryImpl;
import com.adagency.repository.impl.ClientRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CampaignService {
    private final CampaignRepository campaignRepo = new CampaignRepositoryImpl();
    private final ClientRepository clientRepo = new ClientRepositoryImpl();

    private static final Map<CampaignStatus, Set<CampaignStatus>> ALLOWED_TRANSITIONS = Map.of(
            CampaignStatus.CREATED,     EnumSet.of(CampaignStatus.APPROVED, CampaignStatus.CANCELLED),
            CampaignStatus.APPROVED,    EnumSet.of(CampaignStatus.IN_PROGRESS, CampaignStatus.CANCELLED),
            CampaignStatus.IN_PROGRESS, EnumSet.of(CampaignStatus.COMPLETED, CampaignStatus.CANCELLED),
            CampaignStatus.COMPLETED,   EnumSet.noneOf(CampaignStatus.class),
            CampaignStatus.CANCELLED,   EnumSet.noneOf(CampaignStatus.class)
    );

    private void validate(Campaign c) {
        if (c.getTitle() == null || c.getTitle().isBlank())
            throw new ValidationException("Название кампании обязательно.");
        if (c.getBudget() == null || c.getBudget().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Бюджет должен быть больше нуля.");
        if (c.getStartDate() == null || c.getEndDate() == null)
            throw new ValidationException("Даты начала и окончания обязательны.");
        if (!c.getEndDate().isAfter(c.getStartDate()))
            throw new ValidationException("Дата окончания должна быть позже даты начала.");
    }

    public Campaign create(Campaign c) {
        validate(c);
        try {
            if (!clientRepo.existsById(c.getClientId()))
                throw new BusinessException("Клиент id=" + c.getClientId() + " не найден.");
            return campaignRepo.save(c);
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> findAll() {
        try { return campaignRepo.findAll(); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public Campaign findById(int id) {
        try {
            return campaignRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Кампания id=" + id + " не найдена."));
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public void update(Campaign c) {
        validate(c);
        try {
            Campaign old = campaignRepo.findById(c.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Кампания id=" + c.getId() + " не найдена."));
            if (!clientRepo.existsById(c.getClientId()))
                throw new BusinessException("Клиент id=" + c.getClientId() + " не найден.");
            if (old.getStatus() != c.getStatus()) {
                Set<CampaignStatus> allowed = ALLOWED_TRANSITIONS.get(old.getStatus());
                if (!allowed.contains(c.getStatus()))
                    throw new BusinessException("Недопустимый переход статуса: "
                            + old.getStatus() + " -> " + c.getStatus());
            }
            campaignRepo.update(c);
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public void changeStatus(int id, CampaignStatus newStatus) {
        Campaign c = findById(id);
        Set<CampaignStatus> allowed = ALLOWED_TRANSITIONS.get(c.getStatus());
        if (!allowed.contains(newStatus))
            throw new BusinessException("Недопустимый переход статуса: "
                    + c.getStatus() + " -> " + newStatus);
        c.setStatus(newStatus);
        try { campaignRepo.update(c); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public void delete(int id) {
        try {
            if (!campaignRepo.delete(id))
                throw new EntityNotFoundException("Кампания id=" + id + " не найдена.");
        } catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> searchByTitle(String q) {
        try { return campaignRepo.searchByTitle(q); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> filterByStatus(CampaignStatus s) {
        try { return campaignRepo.findByStatus(s); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> filterByType(CampaignType t) {
        try { return campaignRepo.findByType(t); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> filterByClient(int clientId) {
        try { return campaignRepo.findByClientId(clientId); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> filterByBudgetRange(BigDecimal min, BigDecimal max) {
        try { return campaignRepo.findByBudgetRange(min, max); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }

    public List<Campaign> sortByBudgetAsc() {
        return findAll().stream()
                .sorted(Comparator.comparing(Campaign::getBudget))
                .collect(Collectors.toList());
    }

    public List<Campaign> sortByBudgetDesc() {
        return findAll().stream()
                .sorted(Comparator.comparing(Campaign::getBudget).reversed())
                .collect(Collectors.toList());
    }

    public List<Campaign> sortByStartDate() {
        return findAll().stream()
                .sorted(Comparator.comparing(Campaign::getStartDate))
                .collect(Collectors.toList());
    }

    public List<Campaign> sortByTitle() {
        return findAll().stream()
                .sorted(Comparator.comparing(Campaign::getTitle))
                .collect(Collectors.toList());
    }

    public Map<String, String> buildStatistics() {
        List<Campaign> all = findAll();
        int total = all.size();
        long active    = all.stream().filter(c -> c.getStatus() == CampaignStatus.IN_PROGRESS).count();
        long completed = all.stream().filter(c -> c.getStatus() == CampaignStatus.COMPLETED).count();
        long cancelled = all.stream().filter(c -> c.getStatus() == CampaignStatus.CANCELLED).count();
        long tvCount   = all.stream().filter(c -> c.getType()   == CampaignType.TV).count();

        BigDecimal totalBudget = all.stream()
                .map(Campaign::getBudget)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgBudget = total == 0
                ? BigDecimal.ZERO
                : totalBudget.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        Map<String, String> map = new LinkedHashMap<>();
        map.put("Всего клиентов",         String.valueOf(clientCount()));
        map.put("Всего кампаний",         String.valueOf(total));
        map.put("Активных (IN_PROGRESS)", String.valueOf(active));
        map.put("Завершённых",            String.valueOf(completed));
        map.put("Отменённых",             String.valueOf(cancelled));
        map.put("Суммарный бюджет, ₽",    totalBudget.toPlainString());
        map.put("Средний бюджет, ₽",      avgBudget.toPlainString());
        map.put("ТВ-кампаний",            String.valueOf(tvCount));
        return map;
    }

    private int clientCount() {
        try { return clientRepo.findAll().size(); }
        catch (SQLException e) { throw new BusinessException("Ошибка БД: " + e.getMessage()); }
    }
}