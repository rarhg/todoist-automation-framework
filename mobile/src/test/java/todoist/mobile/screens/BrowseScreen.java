package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import todoist.mobile.helpers.GestureHelper;

public class BrowseScreen extends BaseMobileScreen {

    private final By settingsButton = AppiumBy.androidUIAutomator(
            "new UiSelector().description(\"Настройки\")"
    );

    public BrowseScreen(AppiumDriver driver) {
        super(driver);
    }

    public SettingsScreen openSettings() {
        dismissProPromoIfPresent();
        GestureHelper.scrollUntilVisible(driver, settingsButton, 5);
        click(settingsButton);
        return new SettingsScreen(driver);
    }
}