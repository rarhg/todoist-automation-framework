package todoist.web.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectSidebar {

    private final SelenideElement projectsHeader =
            $("[data-expansion-panel-header='true']");
    private final SelenideElement addProjectButton =
            $("[data-expansion-panel-header='true'] button[aria-label='Мои проекты']");
    private final SelenideElement addProjectMenuItem =
            $x("//div[@role='menuitem' and @aria-label='Добавить проект']");

    @Step("Инициировать создание нового проекта через сайдбар с ховером")
    public void initiateNewProjectCreation() {
        projectsHeader.shouldBe(Condition.visible).hover();
        addProjectButton.shouldBe(interactable).click();
        addProjectMenuItem.shouldBe(interactable).click();
    }
}