package ru.company.project.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.company.project.data.DBHelper;
import ru.company.project.pages.CardPage;
import ru.company.project.pages.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.company.project.data.CardTestData.*;


@Epic("Покупка тура по карте")
public class PaymentTest {
    private CardPage paymentPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.holdBrowserOpen = false;
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUpTest() {
        open("http://localhost:8080/");
        MainPage mainPage = new MainPage();
        mainPage.verifyHeaderVisible();
        paymentPage = mainPage.clickBuy();
    }

    @AfterEach
    public void cleanBase() {
        DBHelper.cleanDatabase();
    }

    @Test
    @DisplayName("Система должна успешно проводить оплату APPROVED-картой")
    @Story("Основные сценарии")
    void shouldApprovePaymentWithApprovedCard() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 2, CURRENT_YEAR + 3, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifySuccessNotification(),
                () -> paymentPage.verifyPaymentStatus(STATUS_APPROVED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять оплату DECLINED-картой")
    @Story("Основные сценарии")
    void shouldRejectPaymentWithDeclinedCard() {
        paymentPage.fillingCardForm(DECLINED_CARD, CURRENT_MONTH + 3, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyRejectionNotification(),
                () -> paymentPage.verifyPaymentStatus(STATUS_DECLINED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять оплату несуществующей картой")
    @Story("Валидация номера карты")
    void shouldRejectPaymentWithNonExistentCard() {
        paymentPage.fillingCardForm(INVALID_CARD, CURRENT_MONTH + 1, CURRENT_YEAR + 4, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyRejectionNotification(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна показывать ошибку при коротком номере карты")
    @Story("Валидация номера карты")
    void shouldShowFormatErrorForShortCardNumber() {
        paymentPage.fillingCardForm(SHORT_CARD, CURRENT_MONTH, CURRENT_YEAR + 3, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать номер карты")
    @Story("Валидация номера карты")
    void shouldRequireCardNumberField() {
        paymentPage.fillingCardForm(EMPTY_CARD, CURRENT_MONTH + 2, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять просроченный месяц")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithExpiredMonth() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH - 1, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания месяца")
    @Story("Валидация месяца")
    void shouldShowErrorWhenMonthIsEmpty() {
        paymentPage.fillingCardForm(APPROVED_CARD, EMPTY_MONTH, CURRENT_YEAR + 2, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять некорректный месяц (13)")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithInvalidMonth13() {
        paymentPage.fillingCardForm(APPROVED_CARD, INVALID_MONTH, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять некорректный месяц (00)")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithZerosMonth() {
        paymentPage.fillingCardForm(APPROVED_CARD, MONTH_OF_ZEROS, CURRENT_YEAR + 2, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять просроченный год")
    @Story("Валидация года")
    void shouldRejectExpiredYear() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR - 1, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidYearError(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять срок действия карты, превышающий 5 лет")
    @Story("Валидация года")
    void shouldRejectCardsThatAreMoreThan5YearsOld() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 6, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания года")
    @Story("Валидация года")
    void shouldRequireYearField() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, EMPTY_YEAR, VALID_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания фамилии владельца")
    @Story("Валидация владельца карты")
    void shouldRequireLastNameForHolder() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 4, FIRST_NAME_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кириллицу в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectCyrillicHolder() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 6, CURRENT_YEAR, HOLDER_CYRILLIC, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать заполнения поля владельца")
    @Story("Валидация владельца карты")
    void shouldRequireHolderField() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR, EMPTY_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyThisFieldIsRequired(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять слишком длинное имя владельца (>50 символов)")
    @Story("Валидация владельца карты")
    void shouldRejectLongHolderName() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 7, CURRENT_YEAR + 1, LONG_HOLDER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять спецсимволы в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectSpecialCharactersInHolder() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 8, CURRENT_YEAR, HOLDER_SPECIAL_CHARS, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять цифры в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectNumbersInHolder() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 9, CURRENT_YEAR, HOLDER_NUMBER, VALID_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять короткий CVC (2 цифры)")
    @Story("Валидация CVC")
    void shouldRejectShortCvc() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 3, VALID_HOLDER, CVC_SHORT);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать заполнения поля CVC/CVV")
    @Story("Валидация CVC")
    void shouldRequireCvcField() {
        paymentPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 12, CURRENT_YEAR, VALID_HOLDER, EMPTY_CVC);
        paymentPage.submit();
        assertAll(
                () -> paymentPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }
}