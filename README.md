# «Автоматизация тестирования веб-сервиса, взаимодействующего с СУБД и API Банка»

[![CI Status](https://github.com/levvolkov/tour-pay-gateway-tests/actions/workflows/gradle.yml/badge.svg)](https://github.com/levvolkov/tour-pay-gateway-tests/actions) &nbsp; [![Allure Passed](https://img.shields.io/badge/dynamic/json?url=https://github.com/levvolkov/tour-pay-gateway-tests/raw/main/documentation/allure-report/widgets/summary.json&query=statistic.passed&label=Passed&color=green)](https://levvolkov.github.io/tour-pay-gateway-tests/documentation/allure-report) &nbsp; [![Allure Failed](https://img.shields.io/badge/dynamic/json?url=https://github.com/levvolkov/tour-pay-gateway-tests/raw/main/documentation/allure-report/widgets/summary.json&query=statistic.failed&label=Failed&color=red)](https://levvolkov.github.io/tour-pay-gateway-tests/documentation/allure-report) &nbsp; [![Allure Report](https://img.shields.io/badge/Allure_Report-View-green.svg)](https://levvolkov.github.io/tour-pay-gateway-tests/documentation/allure-report) &nbsp; [![Bugs in Issues](https://img.shields.io/badge/Bug_Reports-View_Issues-red.svg)](https://github.com/levvolkov/tour-pay-gateway-tests/issues)

<br>

## 📌 О проекте

**Автоматизированное тестирование веб-сервиса для покупки туров:**
- **Обычная оплата** по дебетовой карте
- **Кредитование** по данным банковской карты

**Технологии:**

[![Java](https://img.shields.io/badge/Java-11-%23ED8B00?logo=openjdk&logoColor=white)](https://java.com) &nbsp;
[![Gradle](https://img.shields.io/badge/Gradle-7.6-%2302303A?logo=gradle)](https://gradle.org) &nbsp;
[![Selenide](https://img.shields.io/badge/Selenide-6.17.2-%231E90FF?logo=selenium&logoColor=white)](https://selenide.org) &nbsp;
[![JUnit5](https://img.shields.io/badge/JUnit-5.9.2-%2325A162?logo=junit5&logoColor=white)](https://junit.org/junit5/) &nbsp;
[![Allure](https://img.shields.io/badge/Allure-2.21.0-%23FF6A00?logo=testinglibrary&logoColor=white)](https://docs.qameta.io/allure/) &nbsp;
[![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-✓-2671E5?logo=githubactions&logoColor=white)](https://github.com/features/actions)

**Инструменты:**

[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-✓-%23000000?logo=intellijidea)](https://jetbrains.com/idea) &nbsp;
[![Docker](https://img.shields.io/badge/Docker-✓-2496ED?logo=docker)](https://docker.com) &nbsp;
[![DevTools](https://img.shields.io/badge/Chrome_DevTools-✓-2671E5?logo=google-chrome&logoColor=white)](https://developer.chrome.com/docs/devtools/) &nbsp;
[![DBeaver](https://img.shields.io/badge/DBeaver-✓-%23372923?logo=dbeaver)](https://dbeaver.io)

## 🛠 Техническая архитектура
```mermaid
graph LR
    A[Автотесты] --> B[aqa-shop.jar:8080]
    B --> C[Payment Gate]
    B --> D[Credit Gate]
    B --> E[(MySQL:3306)]
    B --> F[(PostgreSQL:5432)]
    C & D --> G[Bank Simulator:9999]
```

## 📂 Структура проекта
```Copy
tour-pay-gateway-tests/
├── artifacts/aqa-shop.jar      # Тестируемый веб-сервис
├── documentation               # Документация
├── gate-simulator/             # Эмулятор банка
├── src/
│   └── test/java/              # Автотесты (UI + DB + API)
├── application.properties      # Конфигурация для подключения к СУБД и банковским сервисам
├── build.gradle                # Файл конфигурации Gradle с зависимостями для Selenide, JUnit, Allure и др.
└── docker-compose.yml          # Контейнеры MySQL + PostgreSQL + Bank Simulator
```

## 📜 Документация
- [План автоматизации](documentation/plan.md)
- [Отчёт по итогам тестировании](documentation/report.md)
- [Отчет по итогам автоматизации](documentation/summary.md)

## 🚀 Процедура запуска автотестов
1. **Клонируйте и откройте проект в IDEA:**
```bash
git clone https://github.com/levvolkov/tour-pay-gateway-tests.git
```

2. **Запустите Docker Desktop и выполните:**
```bash
# Соберет контейнеры MySQL, PostgreSQL, Bank Simulator
docker compose up -d  

# Для запуска приложение на MySQL СУБД
java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar

# Для запуска приложение на PostgreSQL СУБД
java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar
```

3. **Запустите автотесты:**
```bash
# Для MySQL:
./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app" 

# Для PostgreSQL:
./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"

# Для API тестов (если необходимо отдельно)
./gradlew clean test --tests GateSimulatorApiTest

# Просмотр Allure-отчёта:
./gradlew allureServe
```

**Ошибка запуска приложения: если порт занят**
```bash
# Найти PID процесса, использующий порт:
lsof -i :8080

# Принудительное завершение с помощью флага -9:
kill -9 <PID>
```