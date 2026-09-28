package todoist.web.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ConfirmationModal {

    private final SelenideElement modalContainer = $("[data-testid='confirmation-modal']");
    private final SelenideElement confirmButton = $("[data-testid='confirmation-modal'] button[data-autofocus='true']");

    @Step("Проверить, что модальное окно подтверждения открылось")
    public ConfirmationModal verifyModalOpened() {
        modalContainer.shouldBe(visible);
        return this;
    }

    @Step("Подтвердить удаление в модальном окне")
    public void confirm() {
        confirmButton.shouldBe(interactable).click();
        modalContainer.shouldBe(disappear);
    }
}
