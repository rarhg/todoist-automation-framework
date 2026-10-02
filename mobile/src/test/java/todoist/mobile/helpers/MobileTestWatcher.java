package todoist.mobile.helpers;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class MobileTestWatcher implements TestWatcher {

    private static final Logger log = LoggerFactory.getLogger(MobileTestWatcher.class);

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        AppiumExtension.isClassFailed = true;

        try {
            var driver = AppiumExtension.getDriver();

            byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", new ByteArrayInputStream(screenshot));

            Allure.addAttachment("Page source on failure", "text/xml", driver.getPageSource(), ".xml");
        } catch (Exception e) {
            log.error("Не удалось собрать диагностику: {}", e.getMessage());
        }
    }
}
