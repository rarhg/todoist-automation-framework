package todoist.mobile.tests.base;

import org.junit.jupiter.api.BeforeEach;
import todoist.mobile.helpers.AppStateResolver;
import todoist.mobile.helpers.AppiumExtension;

import java.util.Map;

public abstract class BaseAuthTest extends BaseMobileSuite {

    protected BaseAuthTest() {
        super();
    }

    @BeforeEach
    protected void forceAppReset() {
        log.info("Нативный сброс данных приложения перед тестом авторизации");
        driver().executeScript("mobile: clearApp", Map.of("appId", config.appPackage()));
        AppStateResolver.activateApp(driver());
        AppiumExtension.lockPortrait();
    }
}
