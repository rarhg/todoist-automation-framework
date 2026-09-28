package todoist.web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectPage extends BasePage {

    private final SelenideElement projectHeader = $x("//*[@data-testid='large-header']//h1");

    @Step("Проверить, что открылся проект с названием: {expectedName}")
    public ProjectPage verifyProjectHeader(String expectedName) {
        projectHeader.shouldBe(visible).shouldHave(exactText(expectedName));
        return this;
    }
}
