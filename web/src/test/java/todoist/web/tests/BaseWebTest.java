package todoist.web.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.chrome.ChromeOptions;
import todoist.config.ConfigProvider;
import todoist.helpers.Attach;
import todoist.helpers.CookieAuthManager;

import java.util.Map;

@Tag("web")
public abstract class BaseWebTest {

    static {
        Configuration.baseUrl = ConfigProvider.CONFIG.baseUrl();
        Configuration.browser = ConfigProvider.CONFIG.browser().toLowerCase();
        Configuration.browserSize = ConfigProvider.CONFIG.browserSize();
        Configuration.timeout = ConfigProvider.CONFIG.webTimeoutMs();
        Configuration.headless = ConfigProvider.CONFIG.isHeadless();
        Configuration.pageLoadStrategy = ConfigProvider.CONFIG.pageLoadStrategy();

        System.setProperty("webdriver.chrome.silentOutput", "true");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--log-level=3");
        options.addArguments("--silent");

        if (ConfigProvider.CONFIG.isRemote()) {
            Configuration.remote = ConfigProvider.CONFIG.remoteUrl();
            if (ConfigProvider.CONFIG.browserVersion() != null) {
                Configuration.browserVersion = ConfigProvider.CONFIG.browserVersion();
            }
            options.setCapability("selenoid:options", Map.of(
                    "enableVNC", true,
                    "enableVideo", true,
                    "screenResolution", "1920x1080x24"
            ));
        }

        Configuration.browserCapabilities = options;
    }

    @BeforeEach
    public void setUp() {
        CookieAuthManager.ensureGlobalAuth();

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(false));
        Selenide.open("/404");
        CookieAuthManager.injectSession();
        Selenide.refresh();
    }

    @AfterEach
    public void tearDown() {
        Attach.screenshotAs("Last screenshot");
        Attach.pageSource();
        Attach.browserConsoleLogs();
        if (ConfigProvider.CONFIG.isRemote()) {
            Attach.addVideo();
        }
        Selenide.closeWebDriver();
    }
}