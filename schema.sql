-- Выполнить один раз:
--   CREATE DATABASE ad_agency;
--   \c ad_agency
--   \i schema.sql

DROP TABLE IF EXISTS campaigns;
DROP TABLE IF EXISTS clients;

CREATE TABLE clients (
    id          SERIAL PRIMARY KEY,
    full_name   VARCHAR(120) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    phone       VARCHAR(30)  NOT NULL,
    company     VARCHAR(150),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE campaigns (
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(150)  NOT NULL,
    description TEXT,
    budget      NUMERIC(12,2) NOT NULL CHECK (budget > 0),
    status      VARCHAR(20)   NOT NULL,
    type        VARCHAR(30)   NOT NULL,
    start_date  DATE          NOT NULL,
    end_date    DATE          NOT NULL,
    client_id   INTEGER       NOT NULL,
    CONSTRAINT fk_campaign_client
        FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT chk_dates  CHECK (end_date > start_date),
    CONSTRAINT chk_status CHECK (status IN
        ('CREATED','APPROVED','IN_PROGRESS','COMPLETED','CANCELLED')),
    CONSTRAINT chk_type   CHECK (type IN
        ('TV','INTERNET','RADIO','PRINT','OUTDOOR','SOCIAL_MEDIA'))
);

CREATE INDEX idx_campaign_status ON campaigns(status);
CREATE INDEX idx_campaign_type   ON campaigns(type);
CREATE INDEX idx_campaign_client ON campaigns(client_id);

INSERT INTO clients (full_name, email, phone, company) VALUES
('Иван Петров',    'ivan@brand.ru',  '+7-900-111-11-11', 'Brand LLC'),
('Анна Смирнова',  'anna@shop.ru',   '+7-900-222-22-22', 'Shop Market'),
('Олег Кузнецов',  'oleg@auto.ru',   '+7-900-333-33-33', 'Auto Plus'),
('Мария Иванова',  'maria@food.ru',  '+7-900-444-44-44', 'Food Group'),
('Сергей Попов',   'sergey@tech.ru', '+7-900-555-55-55', 'TechSoft');

INSERT INTO campaigns (title, description, budget, status, type, start_date, end_date, client_id) VALUES
('Летняя акция Brand',  'ТВ-ролик 30 сек',       500000.00,'APPROVED',   'TV',          '2025-06-01','2025-08-31',1),
('Реклама ShopMarket',  'Google Ads',             250000.00,'IN_PROGRESS','INTERNET',    '2025-05-01','2025-07-01',2),
('Auto Plus радио',     'Радиоролик 15 сек',      120000.00,'COMPLETED',  'RADIO',       '2025-01-15','2025-03-15',3),
('Food Group соцсети',  'Таргет VK/Telegram',     180000.00,'CREATED',    'SOCIAL_MEDIA','2025-09-01','2025-11-01',4),
('TechSoft баннеры',    'Медийная реклама',       300000.00,'APPROVED',   'INTERNET',    '2025-07-01','2025-10-01',5),
('Brand наружка',       'Билборды в Москве',      400000.00,'IN_PROGRESS','OUTDOOR',     '2025-06-10','2025-09-10',1),
('Shop Market журнал',  'Печатная реклама',        90000.00,'CANCELLED',  'PRINT',       '2025-04-01','2025-06-01',2),
('Auto Plus ТВ',        'ТВ-спонсорство',         750000.00,'CREATED',    'TV',          '2025-10-01','2025-12-31',3),
('Food Group радио',    'Утренние эфиры',         110000.00,'APPROVED',   'RADIO',       '2025-08-01','2025-10-01',4),
('TechSoft соцсети',    'Инфлюенсеры',            220000.00,'IN_PROGRESS','SOCIAL_MEDIA','2025-06-15','2025-09-15',5);