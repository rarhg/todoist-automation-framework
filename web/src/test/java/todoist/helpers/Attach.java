package todoist.helpers;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import static todoist.config.ConfigProvider.CONFIG;

public final class Attach {

    private static final Logger log = LoggerFactory.getLogger(Attach.class);

    private Attach() {
    }

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] screenshotAs(String attachName) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            return ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }

    @Attachment(value = "Page source", type = "text/plain")
    public static byte[] pageSource() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            return WebDriverRunner.getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
        }
        return new byte[0];
    }

    @Attachment(value = "Console logs", type = "text/plain")
    public static String browserConsoleLogs() {
        if (WebDriverRunner.hasWebDriverStarted() && !WebDriverRunner.isFirefox()) {
            try {
                LogEntries logEntries = WebDriverRunner.getWebDriver().manage().logs().get(LogType.BROWSER);
                StringBuilder logs = new StringBuilder();

                logEntries.getAll().stream()
                        .filter(log -> log.getLevel().intValue() >= Level.SEVERE.intValue())
                        .forEach(log -> logs.append(log.getMessage()).append("\n"));

                return logs.toString();
            } catch (Exception e) {
                return String.format("Could not retrieve console logs: %s", e.getMessage());
            }
        }
        return "Console logs not supported for this browser context.";
    }

    @Attachment(value = "Video", type = "text/html", fileExtension = ".html")
    public static String addVideo() {
        return String.format(
                "<html><body><video width='100%%' height='100%%' controls autoplay><source src='%s' type='video/mp4'></video></body></html>",
                getVideoUrl()
        );
    }

    public static URL getVideoUrl() {
        String remoteUrl = CONFIG.remoteUrl();
        if (remoteUrl == null || remoteUrl.isBlank()) {
            return null;
        }

        String videoUrl = remoteUrl.replace("/wd/hub", "/video/") + Selenide.sessionId() + ".mp4";
        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            log.error(String.format("Failed to create video URL: %s", e.getMessage()));
        }
        return null;
    }
}