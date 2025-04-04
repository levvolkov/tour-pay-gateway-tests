package ru.company.project.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


public class MainPage {
    private final SelenideElement buyButton = $(byText("Купить"));
    private final SelenideElement buyOnCreditButton = $(byText("Купить в кредит"));
    private final SelenideElement headingJourneyOfTheDay = $(byText("Путешествие дня"));
    private final SelenideElement paymentCardText = $(byText("Оплата по карте"));
    private final SelenideElement creditCardText = $(byText("Кредит по данным карты"));

    @Step("Нажать: \"Купить\"")
    public CardPage clickBuy() {
        buyButton.shouldBe(visible).click();
        paymentCardText.shouldBe(visible);
        return new CardPage();
    }

    @Step("Нажать кнопку \"Купить в кредит\"")
    public CardPage clickBuyOnCredit() {
        buyOnCreditButton.shouldBe(visible).click();
        creditCardText.shouldBe(visible);
        return new CardPage();
    }

    @Step("Отображения заголовка: \"Путешествие дня\"")
    public void verifyHeaderVisible() {
        headingJourneyOfTheDay.shouldBe(visible);
    }
}