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
