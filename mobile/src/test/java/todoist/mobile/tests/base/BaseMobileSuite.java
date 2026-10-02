package todoist.mobile.tests.base;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskProductionSteps;
import todoist.config.ConfigProvider;
import todoist.config.ProjectConfig;
import todoist.mobile.helpers.AppStateResolver;
import todoist.mobile.helpers.AppStateResolver.AppState;
import todoist.mobile.helpers.AppiumExtension;
import todoist.mobile.helpers.MobileTestWatcher;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

import java.time.Duration;

import static todoist.mobile.helpers.AppStateResolver.APP_READY_TIMEOUT;

@Tag("android")
@ExtendWith({AppiumExtension.class, MobileTestWatcher.class})
public abstract class BaseMobileSuite {

    protected static final ProjectConfig config = ConfigProvider.CONFIG;

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final TaskProductionSteps apiSteps = new TaskProductionSteps();

    protected BaseMobileSuite() {
    }

    protected AndroidDriver driver() {
        return AppiumExtension.getDriver();
    }

    protected AppState ensureInboxOpened() {
        AppStateResolver.activateApp(driver());
        new WebDriverWait(driver(), Duration.ofSeconds(5))
                .until(d -> !d.getPageSource().isBlank());
        AppStateResolver.hideKeyboardIfShown(driver());

        MainScreen mainScreen = new MainScreen(driver());
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        AppState state = AppStateResolver.resolve(mainScreen, welcomeScreen);
        switch (state) {
            case AUTHENTICATED -> log.info("Сессия активна (Inbox доступен)");
            case STALE_NAVIGATION -> {
                log.info("Сессия активна, но приложение осталось не на экране 'Входящие'. Перезапускаем приложение");
                restartAppToInbox(mainScreen);
            }
            case LOGGED_OUT -> {
                log.info("Сессия отсутствует. Выполняем вход");
                loginViaUi(welcomeScreen, mainScreen);
            }
        }
        mainScreen.dismissNotificationBannerIfPresent();
        return state;
    }

    protected void loginViaUi(WelcomeScreen welcomeScreen, MainScreen mainScreen) {
        welcomeScreen.openEmailMenu()
                .selectLoginFromDropdown()
                .fillCredentials(config.testEmail(), config.testPassword())
                .confirmLogin();
        awaitInbox(mainScreen);
    }

    protected void restartAppToInbox(MainScreen mainScreen) {
        AppStateResolver.restartAppToDefaultScreen(driver(), config.appPackage());
        awaitInbox(mainScreen);
    }

    private void awaitInbox(MainScreen mainScreen) {
        new WebDriverWait(driver(), APP_READY_TIMEOUT)
                .until(d -> mainScreen.isInboxPageDisplayed());
    }

    @AfterEach
    protected void cleanUpTestDataViaApi() {
        log.info("Очистка окружения через API");
        try {
            for (TaskResponse task : apiSteps.getAllActiveTasks()) {
                apiSteps.deleteTask(task.getId());
            }
        } catch (Exception e) {
            log.error("Ошибка API-очистки: {}", e.getMessage());
        }
    }
}
