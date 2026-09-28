package todoist.web.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class TaskDetailsPanel {

    private final SelenideElement taskNameStaticContainer =
            $("[data-action-hint='task-detail-view-edit'][aria-label='Название задачи']");

    private final SelenideElement taskNameInputEditable =
            $("[data-testid='task-detail-editor-container'] [contenteditable='true'][role='textbox']");

    private final SelenideElement closePanelButton =
            $("button[aria-label='Закрыть задачу']");

    @Step("Изменить название задачи на: {newText}")
    public TaskDetailsPanel editTaskName(String newText) {
        taskNameStaticContainer.shouldBe(interactable).click();

        taskNameInputEditable.shouldBe(visible).sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);

        taskNameInputEditable.sendKeys(newText, Keys.ENTER);

        closePanelButton.shouldBe(interactable).click();

        return this;
    }
}
