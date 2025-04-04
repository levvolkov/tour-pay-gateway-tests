package ru.company.project.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.qameta.allure.Allure.step;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.*;
import static ru.company.project.data.CardTestData.STATUS_APPROVED;
import static ru.company.project.data.CardTestData.STATUS_DECLINED;


@Epic("Тестирование API симулятора банковских сервисов")
public class GateSimulatorApiTest {
    private static final String PAYMENT_URL = "/payment";
    private static final String CREDIT_URL = "/credit";
    private static final String APPROVED_CARD_API = "{\"number\":\"4444 4444 4444 4441\"}";
    private static final String DECLINED_CARD_API = "{\"number\":\"4444 4444 4444 4442\"}";
    private static final String INVALID_CARD_API = "{\"number\":\"0000 0000 0000 0000\"}";
    private static final long MAX_RESPONSE_TIME_MS = 500;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:9999";
    }

    @Test
    @DisplayName("Успешное одобрение платежа для валидной карты")
    @Story("APPROVED-карта")
    void shouldApprovePaymentForValidCard() {
        Response response = step("Отправка платежного запроса", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(APPROVED_CARD_API)
                        .when()
                        .post(PAYMENT_URL)
        );

        step("Проверка успешного одобрения платежа", () -> {
            assertEquals(200, response.getStatusCode(), "Статус-код должен быть 200");
            assertEquals(STATUS_APPROVED, response.jsonPath().getString("status"), "Транзакция должна быть одобрена");
            assertNotNull(response.jsonPath().getString("id"), "Должен быть возвращён ID операции");
        });
    }

    @Test
    @DisplayName("Успешное одобрение заявки на кредит")
    @Story("APPROVED-карта")
    void shouldApproveCreditRequest() {
        Response response = step("Отправка кредитного запроса", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(APPROVED_CARD_API)
                        .when()
                        .post(CREDIT_URL)
        );

        step("Проверка успешного одобрения кредитной заявки", () -> {
            assertEquals(200, response.getStatusCode(), "Статус-код должен быть 200");
            assertEquals(STATUS_APPROVED, response.jsonPath().getString("status"), "Заявка на кредит должна быть одобрена");
            assertNotNull(response.jsonPath().getString("id"), "Должен быть возвращён ID операции");
        });
    }

    @Test
    @DisplayName("Проверка времени ответа от оплаты (<500 мс)")
    @Story("APPROVED-карта")
    void shouldRespondQuickly() {
        long responseTime = step("Замер времени ответа от оплаты", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(APPROVED_CARD_API)
                        .when()
                        .post(PAYMENT_URL)
                        .timeIn(TimeUnit.MILLISECONDS)
        );

        step("Проверка времени ответа", () -> {
            assertTrue(responseTime < MAX_RESPONSE_TIME_MS,
                    "Время ответа должно быть меньше 500 мс, фактическое: " + responseTime);
        });
    }

    @Test
    @DisplayName("Проверка времени ответа от кредита (<500 мс)")
    @Story("APPROVED-карта")
    void shouldCheckCreditResponseTime() {
        long responseTime = step("Замер времени ответа от кредита", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(APPROVED_CARD_API)
                        .when()
                        .post(CREDIT_URL)
                        .timeIn(TimeUnit.MILLISECONDS)
        );

        step("Проверка времени ответа", () -> {
            assertTrue(responseTime < MAX_RESPONSE_TIME_MS,
                    "Время ответа должно быть меньше 500 мс, фактическое: " + responseTime);
        });
    }

    @Test
    @DisplayName("Отклонённая карта для платежа")
    @Story("DECLINED-карта")
    void shouldDeclinePaymentForInvalidCard() {
        Response response = step("Отправка платежного запроса", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(DECLINED_CARD_API)
                        .when()
                        .post(PAYMENT_URL)
        );

        step("Проверка отклонения платежа", () -> {
            assertEquals(200, response.getStatusCode(), "Статус код должен быть 200");
            assertEquals(STATUS_DECLINED, response.jsonPath().getString("status"), "Статус транзакции должен быть DECLINED");
            assertNotNull(response.jsonPath().getString("id"), "ID операции должен быть не null");
        });
    }

    @Test
    @DisplayName("Отказ в кредите")
    @Story("DECLINED-карта")
    void shouldDeclineCreditRequest() {
        Response response = step("Отправка кредитного запроса", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(DECLINED_CARD_API)
                        .when()
                        .post(CREDIT_URL)
        );

        step("Проверка отказа в кредите", () -> {
            assertEquals(200, response.getStatusCode(), "Статус-код должен быть 200");
            assertEquals(STATUS_DECLINED, response.jsonPath().getString("status"), "Транзакция должна быть отклонена");
            assertNotNull(response.jsonPath().getString("id"), "Должен быть возвращён идентификатор операции");
        });
    }

    @Test
    @DisplayName("Проверка времени ответа от покупки (<500 мс)")
    @Story("DECLINED-карта")
    void shouldHandleDeclinedCardQuickly() {
        long processingTime = step("Измерение времени отклика", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(DECLINED_CARD_API)
                        .when()
                        .post(PAYMENT_URL)
                        .timeIn(TimeUnit.MILLISECONDS)
        );

        step("Проверка времени ответа", () -> {
            assertTrue(processingTime < MAX_RESPONSE_TIME_MS, String.format(
                    "Время отклика должно быть менее %dms, фактически: %dms",
                    MAX_RESPONSE_TIME_MS, processingTime));
        });
    }

    @Test
    @DisplayName("Проверка времени ответа от кредита (<500 мс)")
    @Story("DECLINED-карта")
    void shouldCheckCreditResponseTimeForDeclinedCard() {
        long responseTime = step("Замер времени ответа от кредита", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(DECLINED_CARD_API)
                        .when()
                        .post(CREDIT_URL)
                        .timeIn(TimeUnit.MILLISECONDS)
        );

        step("Проверка времени ответа", () -> {
            assertTrue(responseTime < MAX_RESPONSE_TIME_MS,
                    "Время ответа должно быть меньше 500 мс, фактическое: " + responseTime);
        });
    }

    @Test
    @DisplayName("Ошибка 400 за недействительную карту")
    @Story("Невалидная карта")
    void shouldReturn400ForInvalidCardDetails() {
        Response response = step("Отправка запроса с невалидными данными карты", () ->
                RestAssured.given()
                        .contentType(JSON)
                        .body(INVALID_CARD_API)
                        .when()
                        .post(PAYMENT_URL)
        );

        step("Получена ошибка 400 для недействительной карты", () -> {
            assertEquals(400, response.getStatusCode(),
                    "Для невалидных данных карты должен возвращаться статус 400");
        });
    }
}