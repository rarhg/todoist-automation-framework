package todoist.web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProjectsListPage extends BasePage {

    private static final String PROJECT_MODAL_CONTEXT = "[data-testid='modal-overlay']";

    private final SelenideElement projectNameField = $(PROJECT_MODAL_CONTEXT + " input[name='name']");
    private final SelenideElement projectDescriptionField = $(PROJECT_MODAL_CONTEXT + " .ProseMirror");
    private final SelenideElement submitProjectButton = $(PROJECT_MODAL_CONTEXT + " button[type='submit']");

    @Step("Ввести название проекта: {name}")
    public ProjectsListPage enterProjectName(String name) {
        projectNameField.shouldBe(visible).setValue(name);
        return this;
    }

    @Step("Ввести описание проекта: {description}")
    public ProjectsListPage enterProjectDescription(String description) {
        projectDescriptionField.shouldBe(visible).setValue(description);
        return this;
    }

    @Step("Нажать кнопку 'Добавить' (подтвердить создание проекта)")
    public void submitProjectCreation() {
        submitProjectButton.shouldBe(interactable).click();
    }
}
