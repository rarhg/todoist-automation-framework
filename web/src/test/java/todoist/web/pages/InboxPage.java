package todoist.web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class InboxPage extends BasePage {

    private final SelenideElement pageHeader = $("[data-testid='large-header'] h1");
    private final SelenideElement inboxLink = $("#filter_inbox a");
    private final SelenideElement deleteMenuOption = $("[data-action-hint='task-overflow-menu-delete']");

    private final By taskCompleteCheckbox = By.cssSelector("[data-action-hint='task-complete']");
    private final By taskMoreMenuButton = By.cssSelector("[data-testid='more_menu']");

    private static final String TASK_ROW_XPATH_TEMPLATE = "//li[@data-testid='task-list-item' and .//*[contains(text(), '%s')]]";

    private SelenideElement getTaskRow(String taskName) {
        return $x(String.format(TASK_ROW_XPATH_TEMPLATE, taskName));
    }

    @Step("Проверить, что открыта страница списков задач")
    public InboxPage verifyPageOpened() {
        pageHeader.shouldBe(visible, Duration.ofSeconds(20)).shouldHave(text("Входящие"));
        return this;
    }

    @Step("Нажать на ссылку 'Входящие' in сайдбаре")
    public InboxPage clickInboxLink() {
        inboxLink.shouldBe(interactable).click();
        return this;
    }

    @Step("Нажать кнопку 'Добавить задачу'")
    public InboxPage clickAddTask() {
        $(byText("Добавить задачу")).closest("button").shouldBe(interactable).click();
        return this;
    }

    @Step("Проверить, что задача с именем '{taskName}' отображается во Входящих")
    public InboxPage verifyTaskIsPresent(String taskName) {
        getTaskRow(taskName).shouldBe(visible, Duration.ofSeconds(12));
        return this;
    }

    @Step("Завершить задачу с именем: {taskName}")
    public InboxPage completeTask(String taskName) {
        getTaskRow(taskName).$(taskCompleteCheckbox).shouldBe(interactable).click();
        return this;
    }

    @Step("Открыть меню действий задачи (три точки) с именем: {taskName}")
    public InboxPage openTaskMoreMenu(String taskName) {
        SelenideElement taskRow = getTaskRow(taskName);
        taskRow.shouldBe(visible, Duration.ofSeconds(10)).hover();
        taskRow.$(taskMoreMenuButton).shouldBe(interactable).click();
        return this;
    }

    @Step("Нажать 'Удалить' в контекстном меню задачи")
    public InboxPage clickDeleteInMenu() {
        deleteMenuOption.scrollIntoView(true).shouldBe(interactable).click();
        return this;
    }

    @Step("Проверить, что задача '{taskName}' исчезла из списка")
    public InboxPage verifyTaskIsNotPresent(String taskName) {
        getTaskRow(taskName).should(disappear, Duration.ofSeconds(10));
        return this;
    }

    @Step("Открыть детали задачи: {taskName}")
    public InboxPage openTaskDetails(String taskName) {
        getTaskRow(taskName).$x(".//*[contains(text(), '" + taskName + "')]").shouldBe(interactable).click();
        return this;
    }
}
