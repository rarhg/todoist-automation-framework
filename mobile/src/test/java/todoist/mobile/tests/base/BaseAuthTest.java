package todoist.mobile.tests.base;

import org.junit.jupiter.api.BeforeEach;
import java.util.Map;

public abstract class BaseAuthTest extends BaseMobileSuite {

    protected BaseAuthTest() {
        super();
    }

    @BeforeEach
    protected void forceAppReset() {
        System.out.println("[Lifecycle] Чистый нативный сброс данных перед тестом авторизации...");
        driver().executeScript("mobile: clearApp", Map.of("appId", "com.todoist"));
        driver().executeScript("mobile: startActivity", Map.of(
                "component", "com.todoist/.alias.HomeActivityDefault"));
    }
}
