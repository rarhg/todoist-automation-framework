package todoist.mobile.tests.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.ui.WebDriverWait;
import todoist.config.ConfigProvider;
import todoist.config.ProjectConfig;
import todoist.mobile.helpers.AppStateResolver;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

import java.time.Duration;
import java.util.Map;

import static todoist.mobile.helpers.AppStateResolver.APP_READY_TIMEOUT;

public abstract class BaseAccountTest extends BaseMobileSuite {

    private static final ProjectConfig config = ConfigProvider.CONFIG;

    protected BaseAccountTest() {
        super();
    }

    @BeforeEach
    protected void ensureUserIsLoggedInBeforeAccountTest() {
        System.out.println("[Lifecycle] [AccountPrecondition] Проверка авторизации перед тестом...");

        driver().executeScript("mobile: startActivity", Map.of(
                "component", "com.todoist/.alias.HomeActivityDefault"));

        new WebDriverWait(driver(), Duration.ofSeconds(5))
                .until(d -> !driver().getPageSource().isBlank());

        AppStateResolver.hideKeyboardIfShown(driver());

        MainScreen mainScreen = new MainScreen(driver());
        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());

        switch (AppStateResolver.resolve(mainScreen, welcomeScreen)) {
            case AUTHENTICATED -> {
                System.out.println("[Precondition] Пользователь уже авторизован. Всё готово к тесту.");
                mainScreen.dismissNotificationBannerIfPresent();
            }
            case STALE_NAVIGATION -> {
                System.out.println("[Precondition] Сессия активна, но приложение осталось не на экране "
                        + "'Входящие'. Перезапускаем приложение...");
                AppStateResolver.restartAppToDefaultScreen(driver(), config.appPackage());

                new WebDriverWait(driver(), APP_READY_TIMEOUT )
                        .until(d -> mainScreen.isInboxPageDisplayed());

                mainScreen.dismissNotificationBannerIfPresent();
                System.out.println("[Precondition] Приложение перезапущено, экран 'Входящие' восстановлен.");
            }
            case LOGGED_OUT -> {
                System.out.println("[Precondition] Пользователь не авторизован! Выполняем превентивный вход...");

                welcomeScreen.openEmailMenu()
                        .selectLoginFromDropdown()
                        .fillCredentials(config.testEmail(), config.testPassword())
                        .confirmLogin();

                new WebDriverWait(driver(), APP_READY_TIMEOUT )
                        .until(d -> mainScreen.isInboxPageDisplayed());

                mainScreen.dismissNotificationBannerIfPresent();
                System.out.println("[Precondition] Вход выполнен успешно. База подготовлена.");
            }
        }
    }

    @AfterEach
    protected void restoreSessionAfterLogoutTest() {
        System.out.println("[Lifecycle] [AccountPostcondition] Восстановление сессии для последующих тестов...");

        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());
        MainScreen mainScreen = new MainScreen(driver());

        if (mainScreen.isInboxPageDisplayed()) {
            return;
        }

        if (!welcomeScreen.isWelcomeScreenVisible()) {
            System.out.println("[Postcondition] Сессия активна, но приложение осталось не на экране "
                    + "'Входящие'. Перезапускаем приложение...");
            AppStateResolver.restartAppToDefaultScreen(driver(), config.appPackage());
            try {
                new WebDriverWait(driver(), APP_READY_TIMEOUT )
                        .until(d -> mainScreen.isInboxPageDisplayed());
                System.out.println("[Postcondition] Приложение перезапущено, экран 'Входящие' восстановлен.");
            } catch (Exception e) {
                System.err.println("[Postcondition] Критическая ошибка: не удалось восстановить экран 'Входящие': " + e.getMessage());
            }
            return;
        }

        System.out.println("[Postcondition] Сессия разрушена (как и ожидал тест). Выполняем повторный вход...");

        welcomeScreen.openEmailMenu()
                .selectLoginFromDropdown()
                .fillCredentials(config.testEmail(), config.testPassword())
                .confirmLogin();

        try {
            new WebDriverWait(driver(), APP_READY_TIMEOUT )
                    .until(d -> mainScreen.isInboxPageDisplayed());
            System.out.println("[Postcondition] Авторизация успешно восстановлена. Сьют в безопасности.");
        } catch (Exception e) {
            System.err.println("[Postcondition] Критическая ошибка: не удалось восстановить авторизацию: " + e.getMessage());
        }
    }
}