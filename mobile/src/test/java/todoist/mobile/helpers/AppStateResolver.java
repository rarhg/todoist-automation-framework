package todoist.mobile.helpers;

import io.appium.java_client.android.AndroidDriver;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

public final class AppStateResolver {

    public static final java.time.Duration APP_READY_TIMEOUT = java.time.Duration.ofSeconds(30);

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
        driver.activateApp(appPackage);
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