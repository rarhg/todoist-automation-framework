package todoist.mobile.helpers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.ScreenOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.config.ConfigProvider;
import todoist.config.ProjectConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppiumExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Logger log = LoggerFactory.getLogger(AppiumExtension.class);

    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriverLocalService> serverThreadLocal = new ThreadLocal<>();
    private static final ProjectConfig config = ConfigProvider.CONFIG;

    private static final String RUN_ID = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MM-dd_HH-mm"));

    private static final List<String> ANIMATION_SETTINGS = List.of(
            "window_animation_scale", "transition_animation_scale", "animator_duration_scale");

    private static final List<String> AUTOFILL_COMMANDS = List.of(
            "adb shell settings put secure autofill_service null",
            "adb shell settings put secure autofill_field_classification 0",
            "adb shell settings put secure autofill_feature_field_classification 0",
            "adb shell settings put secure credential_manager_enabled 0",
            "adb shell settings put secure credential_service null",
            "adb shell settings put secure credential_service_primary null"
    );

    public static boolean isClassFailed = false;

    public static AndroidDriver getDriver() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("Драйвер не инициализирован для текущего потока!");
        }
        return driver;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ConfigProvider.refresh();
        isClassFailed = false;

        String provider = provider();
        if ("local".equals(provider)) {
            initLocalDriver();
        } else if ("browserstack".equals(provider)) {
            initBrowserStackDriver(context.getRequiredTestClass().getSimpleName());
        } else {
            throw new IllegalArgumentException("Неподдерживаемый мобильный провайдер: " + provider);
        }
    }

    private static String provider() {
        return config.mobileProvider().toLowerCase();
    }

    private void initLocalDriver() {
        runAdb("adb kill-server");
        runAdb("adb start-server");
        AUTOFILL_COMMANDS.forEach(AppiumExtension::runAdb);
        setAnimationScale(0);
        runAdb("adb shell service call alarm 3 s16 " + config.deviceTimeZone());
        log.info("Часовой пояс устройства: {}", config.deviceTimeZone());

        AppiumDriverLocalService localServer = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingAnyFreePort()
                .withTimeout(Duration.ofMinutes(2))
                .build();
        localServer.start();
        serverThreadLocal.set(localServer);

        UiAutomator2Options options = new UiAutomator2Options();
        options.setCapability("appium:androidHome", config.androidSdkHome());
        options.setDeviceName(config.localDeviceName());
        options.setAppPackage(config.appPackage());
        options.setAppActivity(config.appActivity());
        options.setNoReset(true);
        options.setSkipDeviceInitialization(true);
        boolean isCleanInstallNeeded = Boolean.parseBoolean(System.getProperty("clean.install", "false"));
        if (isCleanInstallNeeded && config.localApkPath() != null && !config.localApkPath().isEmpty()) {
            options.setApp(config.localApkPath());
            options.setNoReset(false);
        }
        options.setCapability("appium:language", "ru");
        options.setCapability("appium:locale", "RU");
        options.setCapability("appium:autoGrantPermissions", true);

        AndroidDriver driver = new AndroidDriver(localServer.getUrl(), options);
        driverThreadLocal.set(driver);
        grantNotificationPermission(driver);
    }

    private void initBrowserStackDriver(String sessionName) throws MalformedURLException {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("platformName", "android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:app", config.browserstackAppUrl());
        capabilities.setCapability("appium:deviceName", config.browserstackDevice());
        capabilities.setCapability("platformVersion", config.browserstackOsVersion());
        capabilities.setCapability("appium:noReset", true);
        capabilities.setCapability("appium:language", "ru");
        capabilities.setCapability("appium:locale", "RU");
        capabilities.setCapability("appium:autoGrantPermissions", true);

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", config.browserstackUsername());
        bstackOptions.put("accessKey", config.browserstackAccessKey());
        bstackOptions.put("projectName", config.browserstackProjectName());
        bstackOptions.put("buildName", config.browserstackBuildName() + "_" + RUN_ID);
        bstackOptions.put("sessionName", sessionName);
        bstackOptions.put("timezone", config.browserstackTimezone());
        capabilities.setCapability("bstack:options", bstackOptions);

        URL remoteUrl = new URL(String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                config.browserstackUsername(), config.browserstackAccessKey()));
        AndroidDriver driver = new AndroidDriver(remoteUrl, capabilities);
        driverThreadLocal.set(driver);
        grantNotificationPermission(driver);
    }

    private void grantNotificationPermission(AndroidDriver driver) {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("action", "grant");
            args.put("appPackage", config.appPackage());
            args.put("permissions", List.of("android.permission.POST_NOTIFICATIONS"));
            driver.executeScript("mobile: changePermissions", args);
            log.info("Разрешение на уведомления выдано");
        } catch (Exception e) {
            log.warn("Не удалось выдать разрешение на уведомления: {}", e.getMessage());
        }
    }

    private static void setAnimationScale(int scale) {
        for (String setting : ANIMATION_SETTINGS) {
            runAdb("adb shell settings put global " + setting + " " + scale);
        }
    }

    private static void runAdb(String command) {
        try {
            Runtime.getRuntime().exec(command).waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Команда прервана: {}", command);
        } catch (Exception e) {
            log.warn("Команда не выполнена (может не поддерживаться этой версией Android): {} -> {}",
                    command, e.getMessage());
        }
    }

    public static void lockPortrait() {
        AndroidDriver d = getDriver();
        try {
            if (d.getOrientation() != ScreenOrientation.PORTRAIT) {
                d.rotate(ScreenOrientation.PORTRAIT);
            }
        } catch (Exception e) {
            log.warn("Не удалось зафиксировать портрет: {}", e.getMessage());
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if ("local".equals(provider())) {
            setAnimationScale(1);
        }

        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                if ("browserstack".equals(provider())) {
                    String status = isClassFailed ? "failed" : "passed";
                    String reason = isClassFailed ? "One or more tests failed in this class" : "All tests passed";

                    driver.executeScript(
                            "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": " +
                                    "{\"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}"
                    );
                }
            } catch (Exception e) {
                log.warn("Не удалось отправить статус сессии в BrowserStack: {}", e.getMessage());
            }

            try {
                driver.quit();
            } finally {
                driverThreadLocal.remove();
            }
        }

        if (serverThreadLocal.get() != null) {
            try {
                serverThreadLocal.get().stop();
            } finally {
                serverThreadLocal.remove();
            }
        }
    }
}
