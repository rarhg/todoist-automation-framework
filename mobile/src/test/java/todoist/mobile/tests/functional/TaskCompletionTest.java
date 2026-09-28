package todoist.mobile.tests.functional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.tests.base.BaseFunctionalTest;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление задачами")
@Feature("Mobile: Выполнение задач")
@Tag("androidLocal")
@Story("Отметка задачи выполненной")
@DisplayName("Тесты на выполнение задач в мобильном приложении")
public class TaskCompletionTest extends BaseFunctionalTest {

    @Test
    @DisplayName("Отметка задачи выполненной через динамический локатор")
    public void shouldCompleteSpecificTaskByName() {
        String uniqueTaskName = "Task_" + System.currentTimeMillis();

        System.out.println("[Precondition] Создание тестовой задачи через API: " + uniqueTaskName);
        apiSteps.createTask(uniqueTaskName);

        MainScreen mainScreen = new MainScreen(driver());

        mainScreen.swipeDownToRefresh();

        mainScreen.completeTaskByName(uniqueTaskName);

        mainScreen.swipeDownToRefresh();

        assertThat(mainScreen.isTaskNotVisible(uniqueTaskName))
                .as("Задача '" + uniqueTaskName + "' должна исчезнуть с экрана после выполнения")
                .isTrue();
    }
}