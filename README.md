# Планировщик задач
Планировщик задач с функцией оповещения пользователя о его продуктивности.

## Использованные технологии
* Java
* Spring Boot, Spring Security, Spring Kafka, Spring Data JPA, Spring Mail
* PostgreSQL, Apache Kafka
* Maven, Docker
* OpenAPI, Swagger UI
* Junit, Mockito, Testcontainers

## Локальный запуск проекта
* Установить Docker
* Установить Maven
* Склоинровать репозиторий
* Вставить SMTP данные в .env файл
* Запустить проект
```
mvn verify
```
```
docker compose up
```

## Запуск проекта в IDE
* В Intellij Idea или другой похожей IDE импортировать проект
* Сбилдить
* Создать БД и топик в кафке
```
docker run --name users-db -e POSTGRES_USER=user -e POSTGRES_PASSWORD=user -e POSTGRES_DB=users -p 5433:5432 -d postgres:16 
```
```
docker run -p 9092:9092 apache/kafka:3.8.0 
```
```
docker exec -it <навазние_контейнер> /bin/bash
```
```
/opt/kafka/bin/kafka-topics.sh --create --topic report-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
