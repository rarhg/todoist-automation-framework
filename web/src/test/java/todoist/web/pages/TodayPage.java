package todoist.web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class TodayPage extends BasePage {

    private final SelenideElement pageHeader = $x("//h1[contains(., 'Сегодня')]");

    @Step("Проверить, что открылся экран 'Сегодня'")
    public TodayPage verifyPageIsOpened() {
        pageHeader.shouldBe(visible);
        return this;
    }
}
