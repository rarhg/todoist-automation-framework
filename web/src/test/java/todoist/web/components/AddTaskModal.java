package todoist.web.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class AddTaskModal {

    private final SelenideElement taskNameInput =
            $x("//div[@data-testid='quick-add']//div[@aria-label='Название задачи' and @contenteditable='true']");

    private final SelenideElement submitTaskButton =
            $x("//div[@data-testid='quick-add']//button[@aria-label='Добавить задачу']");

    @Step("Ввести название задачи: {name}")
    public AddTaskModal enterTaskName(String name) {
        taskNameInput.shouldBe(visible).setValue(name);
        return this;
    }

    @Step("Нажать кнопку 'Добавить задачу' в модальном окне")
    public void submitTask() {
        submitTaskButton.shouldBe(interactable).click();
    }
}
