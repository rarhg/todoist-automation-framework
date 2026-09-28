package todoist.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskProductionSteps;
import todoist.web.components.AddTaskModal;
import todoist.web.components.ConfirmationModal;
import todoist.web.components.TaskDetailsPanel;
import todoist.web.pages.InboxPage;

import java.util.UUID;

@Epic("Управление задачами")
@Feature("Web: Управление задачами через UI")
@Tag("web")
public class TaskUiTest extends BaseWebTest {

    private final InboxPage inboxPage = new InboxPage();
    private final AddTaskModal addTaskModal = new AddTaskModal();
    private final TaskProductionSteps taskProductionSteps = new TaskProductionSteps();
    private final ThreadLocal<String> currentTaskName = new ThreadLocal<>();
    private final ConfirmationModal confirmationModal = new ConfirmationModal();
    private final TaskDetailsPanel taskDetailsPanel = new TaskDetailsPanel();
    private static final Logger log = LoggerFactory.getLogger(TaskUiTest.class);

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
        if (currentTaskName.get() != null) {
            try {
                TaskResponse[] activeTasks = taskProductionSteps.getAllActiveTasks();
                if (activeTasks != null) {
                    for (TaskResponse task : activeTasks) {
                        if (task.getContent() != null && task.getContent().startsWith(currentTaskName.get())) {
                            taskProductionSteps.deleteTask(task.getId());
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Не удалось очистить задачу через API: {}", e.getMessage());
            } finally {
                currentTaskName.remove();
            }
        }
    }
}
