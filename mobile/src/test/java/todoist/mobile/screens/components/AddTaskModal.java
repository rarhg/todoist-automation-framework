package todoist.mobile.screens.components;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import todoist.mobile.screens.BaseMobileScreen;
import todoist.mobile.screens.MainScreen;

public class AddTaskModal extends BaseMobileScreen {

    private final By taskTitleInput = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/message\")"
    );

    private final By submitTaskButton = AppiumBy.accessibilityId("Добавить");

    public AddTaskModal(AppiumDriver driver) {
        super(driver);
    }

    public AddTaskModal enterTitle(String title) {
        type(taskTitleInput, title);
        return this;
    }

    public MainScreen clickSave() {
        click(submitTaskButton);

        try {
            if (((io.appium.java_client.android.AndroidDriver) driver).isKeyboardShown()) {
                ((io.appium.java_client.android.AndroidDriver) driver).hideKeyboard();
            }
        } catch (Exception e) {
            System.err.println("[Lifecycle] Предупреждение: Не удалось скрыть клавиатуру: " + e.getMessage());
        }

        return new MainScreen(driver);
    }
}
