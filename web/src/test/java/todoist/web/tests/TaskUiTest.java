package todoist.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.api.steps.TaskProductionSteps;
import todoist.web.components.AddTaskModal;
import todoist.web.components.ConfirmationModal;
import todoist.web.components.TaskDetailsPanel;
import todoist.web.pages.InboxPage;

import java.time.Duration;
import java.util.UUID;

@Epic("Управление задачами")
@Feature("Web: Управление задачами через UI")
@Tag("web")
@DisplayName("Web: Управление задачами через UI")
public class TaskUiTest extends BaseWebTest {

    private static final Logger log = LoggerFactory.getLogger(TaskUiTest.class);
    private static final Duration SERVER_SYNC_TIMEOUT = Duration.ofSeconds(20);

    private final InboxPage inboxPage = new InboxPage();
    private final AddTaskModal addTaskModal = new AddTaskModal();
    private final TaskProductionSteps taskProductionSteps = new TaskProductionSteps();
    private final ThreadLocal<String> currentTaskName = new ThreadLocal<>();
    private final ConfirmationModal confirmationModal = new ConfirmationModal();
    private final TaskDetailsPanel taskDetailsPanel = new TaskDetailsPanel();

    @BeforeEach
    public void setUpTaskData() {
        currentTaskName.set("Авто-тест задача " + UUID.randomUUID().toString().substring(0, 8));
    }

    @Test
    @Story("Создание задачи")
    @DisplayName("Создание задачи через UI и проверка её появления во Входящих")
    public void shouldCreateTaskViaUi() {
        String taskName = currentTaskName.get();

        inboxPage.openPage("/app/inbox");
        inboxPage.verifyPageOpened()
                .clickAddTask();

        addTaskModal.enterTaskName(taskName)
                .submitTask();

        inboxPage.clickInboxLink()
                .verifyTaskIsPresent(taskName);

        taskProductionSteps.awaitActiveTaskByContentPrefix(taskName, SERVER_SYNC_TIMEOUT);
    }

    @Test
    @Story("Выполнение задачи")
    @DisplayName("Успешное выполнение задачи через клик по чекбоксу")
    public void shouldCompleteTaskViaUi() {
        String taskName = currentTaskName.get();

        taskProductionSteps.createTask(taskName);

        inboxPage.openPage("/app/inbox");
        inboxPage.verifyPageOpened()
                .verifyTaskIsPresent(taskName)
                .completeTask(taskName)
                .verifyTaskIsNotPresent(taskName);
    }

    @Test
    @Story("Удаление задачи")
    @DisplayName("Успешное удаление задачи через контекстное меню")
    public void shouldDeleteTaskViaUi() {
        String taskName = currentTaskName.get();

        taskProductionSteps.createTask(taskName);

        inboxPage.openPage("/app/inbox");
        inboxPage.verifyPageOpened()
                .verifyTaskIsPresent(taskName)
                .openTaskMoreMenu(taskName)
                .clickDeleteInMenu();

        confirmationModal.verifyModalOpened().confirm();
        inboxPage.verifyTaskIsNotPresent(taskName);
    }

    @Test
    @Story("Редактирование задачи")
    @DisplayName("Изменение названия задачи через панель деталей")
    public void shouldEditTaskNameViaUi() {
        String taskName = currentTaskName.get();
        String newName = taskName + " (изм.)";

        taskProductionSteps.createTask(taskName);

        inboxPage.openPage("/app/inbox");
        inboxPage.verifyPageOpened()
                .verifyTaskIsPresent(taskName)
                .openTaskDetails(taskName);

        taskDetailsPanel.editTaskName(newName);

        inboxPage.verifyTaskIsPresent(newName);
    }

    @AfterEach
    public void cleanUpCreatedData() {
        String name = currentTaskName.get();
        if (name == null) {
            return;
        }
        try {
            taskProductionSteps.findActiveTasksByContentPrefix(name)
                    .forEach(task -> taskProductionSteps.deleteTask(task.getId()));
        } catch (Exception e) {
            log.error("Не удалось очистить задачу '{}' через API: {}", name, e.getMessage());
        } finally {
            currentTaskName.remove();
        }
    }
}
