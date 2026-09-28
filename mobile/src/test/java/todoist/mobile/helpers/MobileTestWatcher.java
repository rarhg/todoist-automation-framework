package todoist.mobile.helpers;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;

public class MobileTestWatcher implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        AppiumExtension.isClassFailed = true;

        try {
            var driver = AppiumExtension.getDriver();

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", new ByteArrayInputStream(screenshot));

            String pageSource = driver.getPageSource();
            Allure.addAttachment("Page source on failure", "text/xml",
                    pageSource, ".xml");
        } catch (Exception e) {
            System.err.println("[MobileTestWatcher] Не удалось собрать диагностику: " + e.getMessage());
        }
    }
}
