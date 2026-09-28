package todoist.mobile.tests.functional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import todoist.api.models.TaskResponse;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.tests.base.BaseFunctionalTest;
import todoist.mobile.tests.functional.data.NlpCase;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление задачами")
@Feature("Mobile: Создание задач (в т.ч. NLP)")
@Tag("androidLocal")
@DisplayName("Тесты на создание задач в мобильном приложении")
public class TaskCreationTest extends BaseFunctionalTest {

    @Test
    @Story("Создание задачи через UI")
    @DisplayName("Успешное создание простой задачи через UI")
    public void shouldSuccessfullyCreateTask() {
        String expectedTaskTitle = "Дипломный проект Автоматизация " + System.currentTimeMillis();
        MainScreen mainScreen = new MainScreen(driver());

        mainScreen.clickAddTaskFab()
                .enterTitle(expectedTaskTitle)
                .clickSave();

        mainScreen.swipeDownToRefresh();

        boolean isTaskVisible = mainScreen.isTaskVisibleOnUi(expectedTaskTitle);
        assertThat(isTaskVisible)
                .as("Созданная задача должна визуально отображаться в списке на UI")
                .isTrue();
    }

    @ParameterizedTest(name = "Проверка NLP: \"{0}\"")
    @Story("Распознавание даты через NLP")
    @MethodSource("todoist.mobile.tests.functional.data.NlpDataProvider#provideDynamicNlpCases")
    @DisplayName("«Умное» распознавание даты при вводе задачи (NLP)")
    public void shouldRecognizeDateViaNlp(NlpCase testCase) {
        MainScreen mainScreen = new MainScreen(driver());

        mainScreen.clickAddTaskFab()
                .enterTitle(testCase.inputPhrase())
                .clickSave();

        mainScreen.swipeDownToRefresh();

        boolean isLocalTaskVisible = mainScreen.isTaskVisibleOnUi(testCase.taskCleanName());
        assertThat(isLocalTaskVisible)
                .as("Задача с очищенным именем '" + testCase.taskCleanName() + "' должна отобразиться на UI")
                .isTrue();

        TaskResponse[] activeTasks = apiSteps.getAllActiveTasks();
        TaskResponse targetTask = null;

        for (TaskResponse task : activeTasks) {
            if (task.getContent().contains(testCase.taskCleanName())) {
                targetTask = task;
                break;
            }
        }

        assertThat(targetTask)
                .as("Созданная NLP-задача не найдена через API-слой бэкенда!")
                .isNotNull();
    }
}