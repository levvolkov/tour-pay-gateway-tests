package ru.company.project.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.company.project.base.BaseTest;
import ru.company.project.data.DBHelper;
import ru.company.project.pages.CardPage;
import ru.company.project.pages.MainPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.company.project.data.CardTestData.*;


@Epic("Покупка тура по карте")
public class PaymentTest extends BaseTest {

    @Override
    protected CardPage initializeCardPage(MainPage mainPage) {
        return cardPage = mainPage.clickBuy();
    }

    @Test
    @DisplayName("Система должна успешно проводить оплату APPROVED-картой")
    @Story("Основные сценарии")
    void shouldApprovePaymentWithApprovedCard() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 2, CURRENT_YEAR + 3, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifySuccessNotification(),
                () -> cardPage.verifyPaymentStatus(STATUS_APPROVED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять оплату DECLINED-картой")
    @Story("Основные сценарии")
    void shouldRejectPaymentWithDeclinedCard() {
        cardPage.fillingCardForm(DECLINED_CARD, CURRENT_MONTH + 3, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyRejectionNotification(),
                () -> cardPage.verifyPaymentStatus(STATUS_DECLINED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять оплату несуществующей картой")
    @Story("Валидация номера карты")
    void shouldRejectPaymentWithNonExistentCard() {
        cardPage.fillingCardForm(INVALID_CARD, CURRENT_MONTH + 1, CURRENT_YEAR + 4, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyRejectionNotification(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна показывать ошибку при коротком номере карты")
    @Story("Валидация номера карты")
    void shouldShowFormatErrorForShortCardNumber() {
        cardPage.fillingCardForm(SHORT_CARD, CURRENT_MONTH, CURRENT_YEAR + 3, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать номер карты")
    @Story("Валидация номера карты")
    void shouldRequireCardNumberField() {
        cardPage.fillingCardForm(EMPTY_CARD, CURRENT_MONTH + 2, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять просроченный месяц")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithExpiredMonth() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH - 1, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания месяца")
    @Story("Валидация месяца")
    void shouldShowErrorWhenMonthIsEmpty() {
        cardPage.fillingCardForm(APPROVED_CARD, EMPTY_MONTH, CURRENT_YEAR + 2, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять некорректный месяц (13)")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithInvalidMonth13() {
        cardPage.fillingCardForm(APPROVED_CARD, INVALID_MONTH, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять некорректный месяц (00)")
    @Story("Валидация месяца")
    void shouldRejectPaymentWithZerosMonth() {
        cardPage.fillingCardForm(APPROVED_CARD, MONTH_OF_ZEROS, CURRENT_YEAR + 2, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять просроченный год")
    @Story("Валидация года")
    void shouldRejectExpiredYear() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR - 1, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidYearError(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять срок действия карты, превышающий 5 лет")
    @Story("Валидация года")
    void shouldRejectCardsThatAreMoreThan5YearsOld() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 6, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания года")
    @Story("Валидация года")
    void shouldRequireYearField() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, EMPTY_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать указания фамилии владельца")
    @Story("Валидация владельца карты")
    void shouldRequireLastNameForHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 4, FIRST_NAME_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кириллицу в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectCyrillicHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 6, CURRENT_YEAR, HOLDER_CYRILLIC, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать заполнения поля владельца")
    @Story("Валидация владельца карты")
    void shouldRequireHolderField() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR, EMPTY_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyThisFieldIsRequired(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять слишком длинное имя владельца (>50 символов)")
    @Story("Валидация владельца карты")
    void shouldRejectLongHolderName() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 7, CURRENT_YEAR + 1, LONG_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять спецсимволы в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectSpecialCharactersInHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 8, CURRENT_YEAR, HOLDER_SPECIAL_CHARS, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять цифры в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectNumbersInHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 9, CURRENT_YEAR, HOLDER_NUMBER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять короткий CVC (2 цифры)")
    @Story("Валидация CVC")
    void shouldRejectShortCvc() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 3, VALID_HOLDER, CVC_SHORT);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать заполнения поля CVC/CVV")
    @Story("Валидация CVC")
    void shouldRequireCvcField() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 12, CURRENT_YEAR, VALID_HOLDER, EMPTY_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }
}