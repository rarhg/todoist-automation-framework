package todoist.mobile.tests.base;

import org.junit.jupiter.api.BeforeEach;
import todoist.mobile.helpers.AppStateResolver.AppState;
import todoist.mobile.screens.MainScreen;

public abstract class BaseFunctionalTest extends BaseMobileSuite {

    protected BaseFunctionalTest() {
        super();
    }

    @BeforeEach
    protected void ensureUserIsAuthenticated() {
        log.info("Контроль сессии перед тестом");
        if (ensureInboxOpened() != AppState.LOGGED_OUT) {
            new MainScreen(driver()).swipeDownToRefresh();
        }
    }
}
