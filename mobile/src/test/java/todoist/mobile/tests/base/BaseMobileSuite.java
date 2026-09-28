package todoist.mobile.tests.base;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskProductionSteps;
import todoist.mobile.helpers.AppiumExtension;
import todoist.mobile.helpers.MobileTestWatcher;

@Tag("android")
@ExtendWith({AppiumExtension.class, MobileTestWatcher.class})
public abstract class BaseMobileSuite {

    protected final TaskProductionSteps apiSteps = new TaskProductionSteps();

    protected BaseMobileSuite() {
    }

    protected AndroidDriver driver() {
        return AppiumExtension.getDriver();
    }

    @AfterEach
    protected void cleanUpTestDataViaApi() {
        System.out.println("[BaseMobileSuite] Инженерия: Очистка окружения через API...");
        try {
            TaskResponse[] tasks = apiSteps.getAllActiveTasks();
            if (tasks != null && tasks.length > 0) {
                for (TaskResponse task : tasks) {
                    apiSteps.deleteTask(task.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("[BaseMobileSuite] Ошибка API-очистки: " + e.getMessage());
        }
    }
}
