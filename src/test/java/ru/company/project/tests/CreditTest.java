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


@Epic("Покупка тура в кредит")
public class CreditTest extends BaseTest {

    @Override
    protected CardPage initializeCardPage(MainPage mainPage){
        return cardPage = mainPage.clickBuyOnCredit();
    }

    @Test
    @DisplayName("Система должна одобрять кредит APPROVED-картой")
    @Story("Основные сценарии")
    void shouldApproveCreditWithApprovedCard() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 1, CURRENT_YEAR + 2, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifySuccessNotification(),
                () -> cardPage.verifyCreditStatus(STATUS_APPROVED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит DECLINED-картой")
    @Story("Основные сценарии")
    void shouldRejectCreditWithDeclinedCard() {
        cardPage.fillingCardForm(DECLINED_CARD, CURRENT_MONTH + 2, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyRejectionNotification(),
                () -> cardPage.verifyCreditStatus(STATUS_DECLINED)
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит несуществующей картой")
    @Story("Валидация номера карты")
    void shouldRejectCreditWithNonExistentCard() {
        cardPage.fillingCardForm(INVALID_CARD, CURRENT_MONTH + 3, CURRENT_YEAR + 3, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyRejectionNotification(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна показывать ошибку при коротком номере карты")
    @Story("Валидация номера карты")
    void shouldShowFormatErrorForShortCardNumberInCredit() {
        cardPage.fillingCardForm(SHORT_CARD, CURRENT_MONTH, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать номер карты для оформления кредита")
    @Story("Валидация номера карты")
    void shouldRequireCardNumberForCredit() {
        cardPage.fillingCardForm(EMPTY_CARD, CURRENT_MONTH, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит с просроченным месяцем")
    @Story("Валидация месяца")
    void shouldRejectCreditWithExpiredMonth() {
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
    void shouldRequireMonthForCredit() {
        cardPage.fillingCardForm(APPROVED_CARD, EMPTY_MONTH, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит с некорректным месяцем (13)")
    @Story("Валидация месяца")
    void shouldRejectCreditWithInvalidMonth13() {
        cardPage.fillingCardForm(APPROVED_CARD, INVALID_MONTH, CURRENT_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит с некорректным месяцем (00)")
    @Story("Валидация месяца")
    void shouldRejectCreditWithZerosMonth() {
        cardPage.fillingCardForm(APPROVED_CARD, MONTH_OF_ZEROS, CURRENT_YEAR + 1, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyCardExpirationDate(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кредит с просроченным годом")
    @Story("Валидация года")
    void shouldRejectExpiredYearOnCredit() {
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
    void shouldRejectCardsCreditThatAreMoreThan5YearsOld() {
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
    void shouldRequireYearFieldsWhenPurchasingOnCredit() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, EMPTY_YEAR, VALID_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать фамилию владельца")
    @Story("Валидация владельца карты")
    void shouldRequireLastNameForCreditHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 12, CURRENT_YEAR, FIRST_NAME_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять кириллицу в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectCyrillicInCreditHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 9, CURRENT_YEAR + 2, HOLDER_CYRILLIC, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать заполнения поля владельца")
    @Story("Валидация владельца карты")
    void shouldRequireHolderFieldOnCredit() {
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
    void shouldRejectLongHolderNameOnCredit() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 7, CURRENT_YEAR, LONG_HOLDER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять спецсимволы в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectSpecialCharsInCreditHolder() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR + 1, HOLDER_SPECIAL_CHARS, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять цифры в поле владельца")
    @Story("Валидация владельца карты")
    void shouldRejectNumbersInTheOwnerField() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 2, CURRENT_YEAR + 3, HOLDER_NUMBER, VALID_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна отклонять короткий CVC")
    @Story("Валидация CVC")
    void shouldRejectShortCvcForCredit() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH + 4, CURRENT_MONTH, VALID_HOLDER, CVC_SHORT);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }

    @Test
    @DisplayName("Система должна требовать CVC")
    @Story("Валидация CVC")
    void shouldRequireCvcForCredit() {
        cardPage.fillingCardForm(APPROVED_CARD, CURRENT_MONTH, CURRENT_YEAR +4, VALID_HOLDER, EMPTY_CVC);
        cardPage.submit();
        assertAll(
                () -> cardPage.verifyInvalidFormatMessage(),
                () -> assertEquals(0, DBHelper.getOrderCount())
        );
    }
}