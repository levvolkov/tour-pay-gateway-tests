package ru.company.project.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ru.company.project.data.DBHelper;
import ru.company.project.data.DataHelper;

import java.sql.SQLException;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.company.project.data.CardTestData.*;


public class CardPage {
    private final SelenideElement cardNumberField = $(byText("Номер карты")).parent().$(".input__control");
    private final SelenideElement monthField = $(byText("Месяц")).parent().$(".input__control");
    private final SelenideElement yearField = $(byText("Год")).parent().$(".input__control");
    private final SelenideElement holderField = $(byText("Владелец")).parent().$(".input__control");
    private final SelenideElement cvcField = $(byText("CVC/CVV")).parent().$(".input__control");
    private final SelenideElement continueButton = $(byText("Продолжить"));
    private final SelenideElement successNotification = $(".notification_status_ok");
    private final SelenideElement errorNotification = $(".notification_status_error");
    private final SelenideElement invalidFormatError = $(byText("Неверный формат")).parent().$(".input__sub");
    private final SelenideElement expiredCardError = $(byText("Неверно указан срок действия карты"));
    private final SelenideElement termCardError = $(byText("Истёк срок действия карты"));
    private final SelenideElement thisFieldIsRequired = $(byText("Поле обязательно для заполнения"));

    @Step("Ввести номер дебетовой карты: {cardNumber}")
    public void enterCardNumber(String cardNumber) {
        cardNumberField.setValue(cardNumber);
    }

    @Step("Ввести месяц: {month}")
    public void enterMonth(String month) {
        monthField.setValue(month);
    }

    @Step("Ввести год: {year}")
    public void enterYear(String year) {
        yearField.setValue(year);
    }

    @Step("Ввести владельца: {holder}")
    public void enterHolder(String holder) {
        holderField.setValue(holder);
    }

    @Step("Ввести CVC: {cvc}")
    public void enterCVC(String cvc) {
        cvcField.setValue(cvc);
    }

    public void fillingCardForm(String card, Object month, Object year, String holder, String cvc) {
        enterCardNumber(card);
        enterMonth(DataHelper.getMonth(month));
        enterYear(DataHelper.getYear(year));
        enterHolder(DataHelper.getHolderNameFormat(holder));
        enterCVC(DataHelper.getCvc(cvc));
    }

    @Step("Нажать кнопку 'Продолжить'")
    public void submit() {
        continueButton.shouldBe(visible).click();
    }

    @Step("Уведомление: \"Успешно Операция одобрена Банком.\"")
    public void verifySuccessNotification() {
        successNotification
                .shouldBe(visible
                        .because("Должно отображаться уведомление об успехе"), Duration.ofSeconds(15))
                .shouldHave(text(BANK_TRANSACTION_APPROVAL_1))
                .shouldHave(text(BANK_TRANSACTION_APPROVAL_2));
    }

    @Step("Уведомление: \"Ошибка! Банк отказал в проведении операции.\"")
    public void verifyRejectionNotification() {
        errorNotification
                .shouldBe(visible
                        .because("Должно отображаться уведомление об ошибке"), Duration.ofSeconds(15))
                .shouldHave(text(BANK_TRANSACTION_REFUSAL_1))
                .shouldHave(text(BANK_TRANSACTION_REFUSAL_2));
    }

    @Step("Проверка статуса {systemType}")
    public void verifyStatus(String systemType, String expectedStatus, String actualStatus) {
        assertEquals(expectedStatus, actualStatus, String.format(
                "Проверка статуса %s завершилась неудачно. Ожидали: <%s>, но получили: <%s>.",
                systemType, expectedStatus, actualStatus
        ));
    }

    public void verifyPaymentStatus(String expectedStatus) throws SQLException {
        verifyStatus("платежной системы (Payment Gate)", expectedStatus, DBHelper.getPaymentStatus());
    }

    public void verifyCreditStatus(String expectedStatus) throws SQLException {
        verifyStatus("кредитной системы (Credit Gate)", expectedStatus, DBHelper.getCreditStatus());
    }

    @Step("Уведомление: \"Неверный формат\"")
    public void verifyInvalidFormatMessage() {
        invalidFormatError.shouldBe(visible);
    }

    @Step("Уведомление: \"Неверно указан срок действия карты\"")
    public void verifyCardExpirationDate() {
        expiredCardError.shouldBe(visible);
    }

    @Step("Уведомление: \"Истёк срок действия карты\"")
    public void verifyInvalidYearError() {
        termCardError.shouldBe(visible);
    }

    @Step("Уведомление: \"Поле обязательно для заполнения\"")
    public void verifyThisFieldIsRequired() {
        thisFieldIsRequired.shouldBe(visible);
    }
}