package todoist.mobile.tests.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.WelcomeScreen;

public abstract class BaseAccountTest extends BaseMobileSuite {

    protected BaseAccountTest() {
        super();
    }

    @BeforeEach
    protected void ensureUserIsLoggedInBeforeAccountTest() {
        log.info("Проверка авторизации перед тестом аккаунта");
        ensureInboxOpened();
    }

    @AfterEach
    protected void restoreSessionAfterLogoutTest() {
        log.info("Восстановление сессии для последующих тестов");

        WelcomeScreen welcomeScreen = new WelcomeScreen(driver());
        MainScreen mainScreen = new MainScreen(driver());

        if (mainScreen.isInboxPageDisplayed()) {
            return;
        }

        try {
            if (!welcomeScreen.isWelcomeScreenVisible()) {
                log.info("Сессия активна, но приложение осталось не на экране 'Входящие'. Перезапускаем приложение");
                restartAppToInbox(mainScreen);
            } else {
                log.info("Сессия разрушена (как и ожидал тест). Выполняем повторный вход");
                loginViaUi(welcomeScreen, mainScreen);
            }
            log.info("Сессия восстановлена, экран 'Входящие' доступен");
        } catch (Exception e) {
            log.error("Критическая ошибка: не удалось восстановить экран 'Входящие'", e);
        }
    }
}
