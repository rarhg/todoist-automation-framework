package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

import java.time.Duration;

public class WelcomeScreen extends BaseMobileScreen {

    private final By emailLoginMenuButton = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Войти через Email\")"
    );
    private final By dropdownRegisterButton = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Зарегистрироваться через Email\")"
    );
    private final By dropdownLoginButton = AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.ScrollView\")" +
                    ".childSelector(new UiSelector().text(\"Войти через Email\"))"
    );

    private final By emailInput = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"email\")"
    );
    private final By passwordInput = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"password\")"
    );

    private final By submitLoginButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"auth_button_tag\")"
    );

    private final By registerTitle = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Введите Email и пароль.\")"
    );

    public WelcomeScreen(AppiumDriver driver) {
        super(driver);
    }

    public WelcomeScreen openEmailMenu() {
        click(emailLoginMenuButton);
        return this;
    }

    public WelcomeScreen selectLoginFromDropdown() {
        click(dropdownLoginButton);
        return this;
    }

    public WelcomeScreen selectRegisterFromDropdown() {
        click(dropdownRegisterButton);
        return this;
    }

    public WelcomeScreen fillCredentials(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        return this;
    }

    public MainScreen confirmLogin() {
        click(submitLoginButton);
        return new MainScreen(driver);
    }

    public boolean isRegisterTitleDisplayed() {
        return isDisplayed(registerTitle);
    }

    public boolean isEmailFieldDisplayed() {
        return isDisplayed(emailInput);
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(passwordInput);
    }

    public boolean isWelcomeScreenVisible() {
        log.debug("Проверяем, находимся ли на WelcomeScreen");
        return isDisplayedWithWait(emailLoginMenuButton, Duration.ofSeconds(10));
    }

    public WelcomeScreen attemptConfirmWithoutValidation() {
        click(submitLoginButton, Duration.ofSeconds(3), false);
        return this;
    }
}
