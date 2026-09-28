package todoist.mobile.tests.base;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.ui.WebDriverWait;
import todoist.config.ConfigProvider;
import todoist.config.ProjectConfig;
import todoist.mobile.helpers.AppStateResolver;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

import java.util.Map;

import static todoist.mobile.helpers.AppStateResolver.APP_READY_TIMEOUT;

public abstract class BaseFunctionalTest extends BaseMobileSuite {

    private static final ProjectConfig config = ConfigProvider.CONFIG;

    protected BaseFunctionalTest() {
        super();
    }

    @BeforeEach
    protected void ensureUserIsAuthenticated() {
        System.out.println("[Lifecycle] [Precondition] Проверка автономности: контроль сессии перед тестом...");

        driver().executeScript("mobile: startActivity", Map.of(
                "component", "com.todoist/.alias.HomeActivityDefault"));

        AppStateResolver.hideKeyboardIfShown(driver());

        MainScreen mainScreen = new MainScreen(driver());
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        switch (AppStateResolver.resolve(mainScreen, welcomeScreen)) {
            case AUTHENTICATED -> {
                System.out.println("[Precondition] Сессия активна (Inbox доступен). Очищаем баннеры.");
                mainScreen.dismissNotificationBannerIfPresent();
                mainScreen.swipeDownToRefresh();
            }
            case STALE_NAVIGATION -> {
                System.out.println("[Precondition] Сессия активна, но приложение осталось не на экране "
                        + "'Входящие' (залипшая навигация с прошлого теста). Перезапускаем приложение...");
                AppStateResolver.restartAppToDefaultScreen(driver(), config.appPackage());

                new WebDriverWait(driver(), APP_READY_TIMEOUT)
                        .until(d -> mainScreen.isInboxPageDisplayed());

                mainScreen.dismissNotificationBannerIfPresent();
                mainScreen.swipeDownToRefresh();
                System.out.println("[Precondition] Приложение перезапущено, экран 'Входящие' восстановлен.");
            }
            case LOGGED_OUT -> {
                System.out.println("[Precondition] Сессия отсутствует! Восстанавливаем авторизацию для текущего теста...");

                welcomeScreen.openEmailMenu()
                        .selectLoginFromDropdown()
                        .fillCredentials(config.testEmail(), config.testPassword())
                        .confirmLogin();

                new WebDriverWait(driver(), APP_READY_TIMEOUT )
                        .until(d -> mainScreen.isInboxPageDisplayed());

                mainScreen.dismissNotificationBannerIfPresent();
                System.out.println("[Precondition] Авторизация автономно восстановлена. Задача изолирована.");
            }
        }
    }
}