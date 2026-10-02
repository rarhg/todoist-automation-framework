package todoist.mobile.helpers;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.config.ConfigProvider;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

import java.time.Duration;
import java.util.Map;

public final class AppStateResolver {

    private static final Logger log = LoggerFactory.getLogger(AppStateResolver.class);

    public static final Duration APP_READY_TIMEOUT = Duration.ofSeconds(30);

    public enum AppState {
        AUTHENTICATED,
        STALE_NAVIGATION,
        LOGGED_OUT
    }

    private AppStateResolver() {
    }

    public static AppState resolve(MainScreen mainScreen, WelcomeScreen welcomeScreen) {
        if (mainScreen.isInboxPageDisplayed()) {
            return AppState.AUTHENTICATED;
        }
        if (!welcomeScreen.isWelcomeScreenVisible()) {
            return AppState.STALE_NAVIGATION;
        }
        return AppState.LOGGED_OUT;
    }

    public static void restartAppToDefaultScreen(AndroidDriver driver, String appPackage) {
        driver.terminateApp(appPackage);
        activateApp(driver);
    }

    public static void activateApp(AndroidDriver driver) {
        String appPackage = ConfigProvider.CONFIG.appPackage();
        try {
            driver.activateApp(appPackage);
        } catch (WebDriverException e) {
            log.warn("activateApp не поддерживается сервером, запускаем Activity напрямую: {}", e.getMessage());
            driver.executeScript("mobile: startActivity",
                    Map.of("intent", appPackage + "/" + ConfigProvider.CONFIG.appActivity()));
        }
    }

    public static void hideKeyboardIfShown(AndroidDriver driver) {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {
        }
    }
}
