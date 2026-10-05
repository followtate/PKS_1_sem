# 🎯 Ad Agency — информационная система рекламного агентства

Консольная информационная система для управления рекламным агентством. Позволяет вести учёт клиентов и рекламных кампаний, выполнять поиск, фильтрацию, сортировку, экспорт в Excel и просматривать статистику.

## 📖 О проекте

**Предметная область:** рекламное агентство.

**Основная функция:** заказ рекламной кампании.

Система поддерживает две связанные сущности:

- **Client** — клиент агентства (заказчик рекламы).
- **Campaign** — рекламная кампания, заказанная клиентом.

Кампания не может существовать без клиента (FOREIGN KEY), у клиента может быть много кампаний.

## ✨ Возможности

| Категория            | Что умеет                                                              |
| -------------------- | ---------------------------------------------------------------------- |
| **CRUD**             | Создание, чтение, обновление, удаление клиентов и кампаний             |
| **Поиск**            | По ФИО, email, компании, названию кампании (LIKE, регистронезависимый) |
| **Фильтрация**       | По статусу клиента, статусу/типу/клиенту/диапазону бюджета кампании    |
| **Сортировка**       | По имени, дате создания, бюджету (↑/↓), дате начала, названию          |
| **Статистика**       | 8 показателей: клиенты, кампании, активные, завершённые, бюджеты и др. |
| **Экспорт**          | В Excel (.xlsx) — кампании и клиенты (Apache POI)                      |
| **Валидация**        | Проверка полей, дат, FK, бизнес-правил переходов статусов              |
| **Обработка ошибок** | Не падает на неверном вводе, все ошибки — человеческим языком          |

## 🏗 Архитектура

Многослойная архитектура. Каждый слой знает только про нижележащий.

```mermaid
flowchart TD
    User([👤 Пользователь]) -->|ввод| UI

    subgraph App["Java Application"]
        UI["🖥 Console UI<br/><code>Main.java</code>"]
        Service["⚙ Service Layer<br/><code>ClientService</code><br/><code>CampaignService</code>"]
        Repo["📦 Repository Layer<br/><code>ClientRepository</code> (interface)<br/><code>CampaignRepository</code> (interface)"]
        Impl["🔌 JDBC Implementations<br/><code>ClientRepositoryImpl</code><br/><code>CampaignRepositoryImpl</code>"]
        DB["🐘 DatabaseManager<br/>Connection pool"]
    end

    DB -.->|jdbc:postgresql| PG[("🛢 PostgreSQL<br/>ad_agency")]

    UI --> Service
    Service --> Repo
    Repo -.->|implements| Impl
    Impl --> DB
```

### Поток данных

```mermaid
sequenceDiagram
    participant U as 👤 Пользователь
    participant M as 🖥 Main
    participant S as ⚙ Service
    participant R as 📦 Repository
    participant D as 🛢 PostgreSQL

    U->>M: Выбор пункта меню
    M->>S: Вызов метода сервиса
    S->>S: Валидация / бизнес-правила
    S->>R: save / findById / update / delete
    R->>D: PreparedStatement (?) + executeQuery
    D-->>R: ResultSet
    R-->>S: Client / List<Client> / Optional
    S-->>M: объект(ы)
    M-->>U: Печать результата
```

---

## 🗄 Схема базы данных

```mermaid
erDiagram
    CLIENTS ||--o{ CAMPAIGNS : "has many"

    CLIENTS {
        int      id          PK "SERIAL"
        varchar  full_name   "NOT NULL"
        varchar  email       "NOT NULL, UNIQUE"
        varchar  phone       "NOT NULL"
        varchar  company
        varchar  status      "ACTIVE | INACTIVE | BLOCKED"
        timestamp created_at "DEFAULT NOW()"
    }

    CAMPAIGNS {
        int      id          PK "SERIAL"
        varchar  title       "NOT NULL"
        text     description
        numeric  budget      "CHECK > 0"
        varchar  status      "CREATED | APPROVED | IN_PROGRESS | COMPLETED | CANCELLED"
        varchar  type        "TV | INTERNET | RADIO | PRINT | OUTDOOR | SOCIAL_MEDIA"
        date     start_date  "NOT NULL"
        date     end_date    "NOT NULL"
        int      client_id   FK "→ CLIENTS.id"
    }
```

### Связи

- **`campaigns.client_id` → `clients.id`** — один ко многим.
- Удалить клиента с кампаниями нельзя (защита на уровне сервиса и FK).

## 🧬 Структура проекта

```
ad-agency/
├── pom.xml
├── schema.sql
├── README.md
└── src/main/java/com/adagency/
    ├── Main.java                        # точка входа, консольное меню
    │
    ├── model/
    │   ├── Client.java                  # модель клиента
    │   ├── Campaign.java                # модель кампании
    │   ├── ClientStatus.java            # enum ACTIVE/INACTIVE/BLOCKED
    │   ├── CampaignStatus.java          # enum статусов кампании
    │   └── CampaignType.java            # enum типов рекламы
    │
    ├── repository/
    │   ├── ClientRepository.java        # интерфейс
    │   ├── CampaignRepository.java      # интерфейс
    │   └── impl/
    │       ├── ClientRepositoryImpl.java     # JDBC реализация
    │       └── CampaignRepositoryImpl.java
    │
    ├── service/
    │   ├── ClientService.java           # бизнес-логика клиентов
    │   └── CampaignService.java         # бизнес-логика кампаний
    │
    ├── exception/
    │   ├── EntityNotFoundException.java
    │   ├── BusinessException.java
    │   └── ValidationException.java
    │
    └── util/
        ├── DatabaseManager.java         # JDBC подключение
        ├── InputHelper.java             # безопасный ввод
        └── ExcelExporter.java           # Apache POI экспорт
```

## 🧠 Бизнес-правила

| #   | Правило                                                                                | Где реализовано                       |
| --- | -------------------------------------------------------------------------------------- | ------------------------------------- |
| 1   | ФИО клиента обязательно, email должен содержать `@`, телефон обязателен                | `ClientService.validate`              |
| 2   | Email клиента уникален (проверка через UNIQUE + обработка SQLException)                | `ClientService.create`                |
| 3   | Нельзя удалить клиента, у которого есть кампании                                       | `ClientService.delete`                |
| 4   | Название кампании обязательно, бюджет > 0                                              | `CampaignService.validate`            |
| 5   | Дата окончания должна быть позже даты начала                                           | `CampaignService.validate`            |
| 6   | Нельзя создать кампанию для несуществующего клиента                                    | `CampaignService.create`              |
| 7   | Разрешённые переходы статуса: `CREATED → APPROVED/CANCELLED → IN_PROGRESS → COMPLETED` | `CampaignService.ALLOWED_TRANSITIONS` |

---

## 🧪 Технологии

| Технология      | Версия | Назначение      |
| --------------- | ------ | --------------- |
| Java            | 17     | Язык            |
| Maven           | 3.8+   | Сборка          |
| PostgreSQL JDBC | 42.7.3 | Драйвер БД      |
| Apache POI      | 5.2.5  | Экспорт в Excel |

---

## 📚 Что демонстрирует проект

- ✅ **ООП:** инкапсуляция (private-поля), наследование (`ValidationException extends BusinessException`), полиморфизм (интерфейсы репозиториев)
- ✅ **Java Collections Framework:** `List`, `Map`, `Set`, `EnumSet`, `LinkedHashMap`
- ✅ **Stream API:** `filter`, `sorted`, `map`, `reduce`, `collect`
- ✅ **Enum:** 3 перечисления с бизнес-логикой
- ✅ **Кастомные исключения:** 3 типа
- ✅ **JDBC:** `Connection`, `PreparedStatement`, `ResultSet`, `try-with-resources`
- ✅ **Многослойная архитектура:** UI → Service → Repository → DB
- ✅ **Параметризованные SQL-запросы:** защита от SQL-инъекций
- ✅ **Экспорт данных:** Apache POI (xlsx)

Инструкция по запуску

1. Подготовить БД

psql -U postgres
CREATE DATABASE ad_agency;
\q
psql -U postgres -d ad_agency -f schema.sql

2. Если пароль от postgres не postgres

В IntelliJ IDEA → Run → Edit Configurations → VM options:

-Ddb.url=jdbc:postgresql://localhost:5432/ad*agency
-Ddb.user=postgres
-Ddb.password=твой*пароль

3. Собрать и запустить

mvn clean package
java -jar target/ad-agency-1.0.jar
