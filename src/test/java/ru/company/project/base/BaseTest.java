package ru.company.project.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.company.project.data.DBHelper;
import ru.company.project.pages.CardPage;
import ru.company.project.pages.MainPage;

import static com.codeborne.selenide.Selenide.open;


public abstract class BaseTest {
    protected CardPage cardPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.holdBrowserOpen = false;
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "false"));
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
        cardPage = initializeCardPage(mainPage);
    }

    @AfterEach
    public void cleanBase() {
        DBHelper.cleanDatabase();
    }

    // Абстрактный метод, который будет реализован в дочерних классах
    protected abstract CardPage initializeCardPage(MainPage mainPage);
}