# Тестовое задание. TestWallet

Текст задания: 
*Напишите приложение, которое по REST принимает запрос вида*
```bash
POST api/v1/wallet
{
valletId: UUID,
operationType: DEPOSIT or WITHDRAW,
amount: 1000
}
```
после выполнять логику по изменению счета в базе данных
*также есть возможность получить баланс кошелька
```bash
GET api/v1/wallets/{WALLET_UUID}
```
*Стек: java 8-17, Spring 3, Postgresql*

*Должны быть написаны миграции для базы данных с помощью liquibase*

*Обратите особое внимание проблемам при работе в конкурентной среде (1000 RPS по
одному кошельку). Ни один запрос не должен быть не обработан (50Х error)*

*Предусмотрите соблюдение формата ответа для заведомо неверных запросов, когда
кошелька не существует, не валидный json, или недостаточно средств.*
*Приложение должно запускаться в докер контейнере, база данных тоже, вся система
должна подниматься с помощью docker-compose*
*Предусмотрите возможность настраивать различные параметры как на стороне
приложения так и базы данных без пересборки контейнеров.*
*Эндпоинты должны быть покрыты тестами.*

## Требования к окружению

1. jdk-17
2. maven2
3. docker-compose

## Запуск приложения

1. Для сборки приложения использовать команду:
```
mvn clean package -P build
```
Для сборки необходим запущенный докер, так как в нём поднимается тестовая БД, в которой применяются миграции и производятся тесты

2. Для запуска приложения и БД в контейнерах:
```
docker-compose up -d
```

## Спецификация OpenAPI
http://localhost:9500/api/swagger-ui/index.html после запуска приложения