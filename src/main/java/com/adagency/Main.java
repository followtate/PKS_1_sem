package com.adagency;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.adagency.exception.BusinessException;
import com.adagency.exception.EntityNotFoundException;
import com.adagency.model.Campaign;
import com.adagency.model.CampaignStatus;
import com.adagency.model.CampaignType;
import com.adagency.model.Client;
import com.adagency.model.ClientStatus;
import com.adagency.service.CampaignService;
import com.adagency.service.ClientService;
import com.adagency.util.ExcelExporter;
import com.adagency.util.InputHelper;

public class Main {

    private static final ClientService   clientService   = new ClientService();
    private static final CampaignService campaignService = new CampaignService();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   РЕКЛАМНОЕ АГЕНТСТВО — ИНФОСИСТЕМА");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputHelper.readInt("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> clientsMenu();
                    case 2 -> campaignsMenu();
                    case 3 -> searchMenu();
                    case 4 -> filterMenu();
                    case 5 -> sortMenu();
                    case 6 -> printStatistics();
                    case 7 -> exportMenu();
                    case 0 -> running = false;
                    default -> System.out.println("Неверный пункт меню.");
                }
            } catch (BusinessException | EntityNotFoundException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
        System.out.println("До свидания!");
    }

    private static void printMainMenu() {
        System.out.println("""
                
                ================ ГЛАВНОЕ МЕНЮ ================
                1. Клиенты
                2. Рекламные кампании
                3. Поиск
                4. Фильтрация
                5. Сортировка
                6. Статистика
                7. Экспорт данных в Excel
                0. Выход
                ==============================================""");
    }

    // ================== КЛИЕНТЫ ==================
    private static void clientsMenu() {
        while (true) {
            System.out.println("""
                    
                    ----------- КЛИЕНТЫ -----------
                    1. Список клиентов
                    2. Добавить клиента
                    3. Найти по ID
                    4. Изменить клиента
                    5. Удалить клиента
                    6. Фильтр по статусу
                    7. Сортировка по имени (↑/↓)
                    8. Сортировка по дате создания (↑/↓)
                    0. Назад""");
            int c = InputHelper.readInt("Выбор: ");
            try {
                switch (c) {
                    case 1 -> printClients(clientService.findAll());
                    case 2 -> addClient();
                    case 3 -> {
                        int id = InputHelper.readInt("ID клиента: ");
                        System.out.println(clientService.findById(id));
                    }
                    case 4 -> updateClient();
                    case 5 -> {
                        int id = InputHelper.readInt("ID клиента: ");
                        clientService.delete(id);
                        System.out.println("Клиент удалён.");
                    }
                    case 6 -> printClients(clientService.filterByStatus(
                            readEnum(ClientStatus.class, "Статус")));
                    case 7 -> {
                        boolean desc = readDirection();
                        printClients(clientService.sortByName(desc));
                    }
                    case 8 -> {
                        boolean desc = readDirection();
                        printClients(clientService.sortByCreatedAt(desc));
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (BusinessException | EntityNotFoundException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void addClient() {
        String name    = InputHelper.readLine("ФИО: ");
        String email   = InputHelper.readLine("Email: ");
        String phone   = InputHelper.readLine("Телефон: ");
        String company = InputHelper.readLine("Компания (Enter — пропустить): ");
        Client c = new Client(name, email, phone, company.isBlank() ? null : company);
        c.setStatus(readEnum(ClientStatus.class, "Статус"));
        clientService.create(c);
        System.out.println("Клиент создан с id=" + c.getId());
    }

    private static void updateClient() {
        int id = InputHelper.readInt("ID клиента: ");
        Client c = clientService.findById(id);
        String name    = InputHelper.readLine("ФИО [" + c.getFullName() + "]: ");
        String email   = InputHelper.readLine("Email [" + c.getEmail() + "]: ");
        String phone   = InputHelper.readLine("Телефон [" + c.getPhone() + "]: ");
        String company = InputHelper.readLine("Компания [" + c.getCompany() + "]: ");
        if (!name.isBlank())    c.setFullName(name);
        if (!email.isBlank())   c.setEmail(email);
        if (!phone.isBlank())   c.setPhone(phone);
        if (!company.isBlank()) c.setCompany(company);
        System.out.print("Сменить статус? (y/N): ");
        if (InputHelper.readLine("").equalsIgnoreCase("y")) {
            c.setStatus(readEnum(ClientStatus.class, "Новый статус"));
        }
        clientService.update(c);
        System.out.println("Клиент обновлён.");
    }

    private static void printClients(List<Client> list) {
        if (list.isEmpty()) { System.out.println("Список пуст."); return; }
        list.forEach(System.out::println);
    }

    // ================== КАМПАНИИ ==================
    private static void campaignsMenu() {
        while (true) {
            System.out.println("""
                    
                    -------- РЕКЛАМНЫЕ КАМПАНИИ --------
                    1. Список кампаний
                    2. Создать кампанию
                    3. Найти по ID
                    4. Изменить кампанию
                    5. Сменить статус
                    6. Удалить кампанию
                    0. Назад""");
            int c = InputHelper.readInt("Выбор: ");
            try {
                switch (c) {
                    case 1 -> printCampaigns(campaignService.findAll());
                    case 2 -> addCampaign();
                    case 3 -> {
                        int id = InputHelper.readInt("ID кампании: ");
                        System.out.println(campaignService.findById(id));
                    }
                    case 4 -> updateCampaign();
                    case 5 -> changeCampaignStatus();
                    case 6 -> {
                        int id = InputHelper.readInt("ID кампании: ");
                        campaignService.delete(id);
                        System.out.println("Кампания удалена.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (BusinessException | EntityNotFoundException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void addCampaign() {
        String title    = InputHelper.readLine("Название: ");
        String desc     = InputHelper.readLine("Описание: ");
        BigDecimal budget = InputHelper.readBigDecimal("Бюджет: ");
        CampaignType type = readEnum(CampaignType.class, "Тип");
        LocalDate start   = InputHelper.readDate("Дата начала (YYYY-MM-DD): ");
        LocalDate end     = InputHelper.readDate("Дата окончания (YYYY-MM-DD): ");
        int clientId      = InputHelper.readInt("ID клиента: ");
        Campaign c = new Campaign(title, desc, budget,
                CampaignStatus.CREATED, type, start, end, clientId);
        campaignService.create(c);
        System.out.println("Кампания создана с id=" + c.getId());
    }

    private static void updateCampaign() {
        int id = InputHelper.readInt("ID кампании: ");
        Campaign c = campaignService.findById(id);
        String title = InputHelper.readLine("Название [" + c.getTitle() + "]: ");
        String desc  = InputHelper.readLine("Описание [" + c.getDescription() + "]: ");
        BigDecimal budget = InputHelper.readOptionalBigDecimal("Бюджет [" + c.getBudget() + "]: ");
        if (!title.isBlank()) c.setTitle(title);
        if (!desc.isBlank())  c.setDescription(desc);
        if (budget != null)   c.setBudget(budget);
        campaignService.update(c);
        System.out.println("Кампания обновлена.");
    }

    private static void changeCampaignStatus() {
        int id = InputHelper.readInt("ID кампании: ");
        CampaignStatus status = readEnum(CampaignStatus.class, "Новый статус");
        campaignService.changeStatus(id, status);
        System.out.println("Статус изменён.");
    }

    private static void printCampaigns(List<Campaign> list) {
        if (list.isEmpty()) { System.out.println("Список пуст."); return; }
        list.forEach(System.out::println);
    }

    // ================== ПОИСК ==================
    private static void searchMenu() {
        while (true) {
            System.out.println("""
                    
                    ------------- ПОИСК -------------
                    1. Клиент по ФИО
                    2. Клиент по email
                    3. Клиент по компании
                    4. Кампания по названию
                    0. Назад""");
            int c = InputHelper.readInt("Выбор: ");
            try {
                switch (c) {
                    case 1 -> printClients(clientService.searchByName(InputHelper.readLine("ФИО: ")));
                    case 2 -> printClients(clientService.searchByEmail(InputHelper.readLine("Email: ")));
                    case 3 -> printClients(clientService.searchByCompany(InputHelper.readLine("Компания: ")));
                    case 4 -> printCampaigns(campaignService.searchByTitle(InputHelper.readLine("Название: ")));
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (BusinessException e) { System.out.println("Ошибка: " + e.getMessage()); }
        }
    }

    // ================== ФИЛЬТРАЦИЯ ==================
    private static void filterMenu() {
        while (true) {
            System.out.println("""
                    
                    ----------- ФИЛЬТРАЦИЯ -----------
                    1. Клиенты по статусу
                    2. Кампании по статусу
                    3. Кампании по типу
                    4. Кампании по клиенту
                    5. Кампании по диапазону бюджета
                    0. Назад""");
            int c = InputHelper.readInt("Выбор: ");
            try {
                switch (c) {
                    case 1 -> printClients(clientService.filterByStatus(
                            readEnum(ClientStatus.class, "Статус")));
                    case 2 -> printCampaigns(campaignService.filterByStatus(
                            readEnum(CampaignStatus.class, "Статус")));
                    case 3 -> printCampaigns(campaignService.filterByType(
                            readEnum(CampaignType.class, "Тип")));
                    case 4 -> printCampaigns(campaignService.filterByClient(
                            InputHelper.readInt("ID клиента: ")));
                    case 5 -> {
                        BigDecimal min = InputHelper.readBigDecimal("Мин. бюджет: ");
                        BigDecimal max = InputHelper.readBigDecimal("Макс. бюджет: ");
                        printCampaigns(campaignService.filterByBudgetRange(min, max));
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (BusinessException e) { System.out.println("Ошибка: " + e.getMessage()); }
        }
    }

    // ================== СОРТИРОВКА ==================
    private static void sortMenu() {
        while (true) {
            System.out.println("""
                    
                    ----------- СОРТИРОВКА -----------
                    1. Клиенты по имени ↑
                    2. Клиенты по имени ↓
                    3. Кампании по бюджету ↑
                    4. Кампании по бюджету ↓
                    5. Кампании по дате начала
                    6. Кампании по названию
                    0. Назад""");
            int c = InputHelper.readInt("Выбор: ");
            try {
                switch (c) {
                    case 1 -> printClients(clientService.sortByName(false));
                    case 2 -> printClients(clientService.sortByName(true));
                    case 3 -> printCampaigns(campaignService.sortByBudgetAsc());
                    case 4 -> printCampaigns(campaignService.sortByBudgetDesc());
                    case 5 -> printCampaigns(campaignService.sortByStartDate());
                    case 6 -> printCampaigns(campaignService.sortByTitle());
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт.");
                }
            } catch (BusinessException e) { System.out.println("Ошибка: " + e.getMessage()); }
        }
    }

    // ================== СТАТИСТИКА ==================
    private static void printStatistics() {
        try {
            Map<String, String> stats = campaignService.buildStatistics();
            System.out.println("\n============ СТАТИСТИКА ============");
            stats.forEach((k, v) -> System.out.printf("%-25s %s%n", k + ":", v));
            System.out.println("====================================");
        } catch (BusinessException e) { System.out.println("Ошибка: " + e.getMessage()); }
    }

    // ================== ЭКСПОРТ ==================
    private static void exportMenu() {
        System.out.println("""
                
                ----------- ЭКСПОРТ -----------
                1. Экспорт кампаний в Excel
                2. Экспорт клиентов в Excel
                0. Назад""");
        int c = InputHelper.readInt("Выбор: ");
        if (c == 0) return;
        String path = InputHelper.readLine("Путь файла (с именем .xlsx): ");
        if (path.isBlank()) {
            path = (c == 1) ? "campaigns.xlsx" : "clients.xlsx";
        }
        try {
            if (c == 1) {
                ExcelExporter.exportCampaigns(campaignService.findAll(), path);
            } else if (c == 2) {
                ExcelExporter.exportClients(clientService.findAll(), path);
            } else {
                System.out.println("Неверный пункт.");
                return;
            }
            System.out.println("Файл сохранён: " + path);
        } catch (IOException e) {
            System.out.println("Ошибка экспорта: " + e.getMessage());
        }
    }

    // ================== УТИЛИТЫ ==================
    private static boolean readDirection() {
        while (true) {
            String s = InputHelper.readLine("Направление (1 — по возрастанию, 2 — по убыванию): ");
            if (s.equals("1")) return false;
            if (s.equals("2")) return true;
            System.out.println("Введите 1 или 2.");
        }
    }

    private static <E extends Enum<E>> E readEnum(Class<E> clazz, String label) {
        E[] values = clazz.getEnumConstants();
        StringBuilder sb = new StringBuilder(label + " (");
        for (int i = 0; i < values.length; i++) {
            sb.append(i + 1).append("=").append(values[i]);
            if (i < values.length - 1) sb.append(", ");
        }
        sb.append("): ");
        while (true) {
            int n = InputHelper.readInt(sb.toString());
            if (n >= 1 && n <= values.length) return values[n - 1];
            System.out.println("Неверный номер.");
        }
    }
}