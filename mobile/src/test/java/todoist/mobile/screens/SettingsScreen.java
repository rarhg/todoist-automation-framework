package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import todoist.mobile.helpers.GestureHelper;

import java.time.Duration;

public class SettingsScreen extends BaseMobileScreen {

    private final By logoutButton = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Выйти\")"
    );

    private final By confirmLogoutButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/button1\")"
    );

    public SettingsScreen(AppiumDriver driver) {
        super(driver);
    }

    public WelcomeScreen logOut() {
        GestureHelper.scrollUntilVisible(driver, logoutButton, 5);
        click(logoutButton);

        if (isDisplayedWithWait(confirmLogoutButton, Duration.ofSeconds(3))) {
            click(confirmLogoutButton);
        }

        return new WelcomeScreen(driver);
    }
}
