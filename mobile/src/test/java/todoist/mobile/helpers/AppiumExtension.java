package todoist.mobile.helpers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.MutableCapabilities;
import todoist.config.ConfigProvider;
import todoist.config.ProjectConfig;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class AppiumExtension implements BeforeAllCallback, AfterAllCallback {

    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriverLocalService> serverThreadLocal = new ThreadLocal<>();
    private static final ProjectConfig config = ConfigProvider.CONFIG;

    private static final String RUN_ID = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("MM-dd_HH-mm"));

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

        String provider = config.mobileProvider().toLowerCase();
        if ("local".equals(provider)) {
            initLocalDriver();
        } else if ("browserstack".equals(provider)) {
            String className = context.getRequiredTestClass().getSimpleName();
            initBrowserStackDriver(className);
        } else {
            throw new IllegalArgumentException("Неподдерживаемый мобильный провайдер: " + provider);
        }
    }

    private void initLocalDriver() {
        try {
            Runtime.getRuntime().exec("adb kill-server").waitFor();
            Runtime.getRuntime().exec("adb start-server").waitFor();
        } catch (Exception e) {
            System.err.println("Ошибка сброса ADB: " + e.getMessage());
        }
        disableAndroidAutofill();
        disableSystemAnimations();
        setDeviceTimeZone();
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
        boolean isCleanInstallNeeded =
                Boolean.parseBoolean(System.getProperty("clean.install", "false"));
        if (isCleanInstallNeeded && config.localApkPath() != null &&
                !config.localApkPath().isEmpty()) {
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

    private void disableSystemAnimations() {
        String[] commands = {
                "adb shell settings put global window_animation_scale 0",
                "adb shell settings put global transition_animation_scale 0",
                "adb shell settings put global animator_duration_scale 0"
        };
        for (String command : commands) {
            try {
                Runtime.getRuntime().exec(command).waitFor();
            } catch (Exception e) {
                System.err.println("[Animations] Не удалось отключить анимацию: "
                        + command + " -> " + e.getMessage());
            }
        }
    }

    private void restoreSystemAnimations() {
        String[] commands = {
                "adb shell settings put global window_animation_scale 1",
                "adb shell settings put global transition_animation_scale 1",
                "adb shell settings put global animator_duration_scale 1"
        };
        for (String command : commands) {
            try {
                Runtime.getRuntime().exec(command).waitFor();
            } catch (Exception e) {
                System.err.println("[Animations] Не удалось восстановить анимацию: "
                        + command + " -> " + e.getMessage());
            }
        }
    }

    private void disableAndroidAutofill() {
        String[] commands = {
                "adb shell settings put secure autofill_service null",
                "adb shell settings put secure autofill_field_classification 0",
                "adb shell settings put secure autofill_feature_field_classification 0",
                "adb shell settings put secure credential_manager_enabled 0",
                "adb shell settings put secure credential_service null",
                "adb shell settings put secure credential_service_primary null"
        };
        for (String command : commands) {
            try {
                Runtime.getRuntime().exec(command).waitFor();
            } catch (Exception e) {
                System.err.println("[Autofill] Команда не выполнена (может не "
                        + "существовать на этой версии Android): " + command + " -> " + e.getMessage());
            }
        }
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
            args.put("permissions", java.util.List.of("android.permission.POST_NOTIFICATIONS"));
            driver.executeScript("mobile: changePermissions", args);
            System.out.println("[Permissions] Разрешение на уведомления выдано успешно.");
        } catch (Exception e) {
            System.err.println("[Permissions] Не удалось выдать разрешение на уведомления: " + e.getMessage());
        }
    }

    private void setDeviceTimeZone() {
        String timeZone = config.deviceTimeZone();
        String command = "adb shell service call alarm 3 s16 " + timeZone;
        try {
            Runtime.getRuntime().exec(command).waitFor();
            System.out.println("[TimeZone] Часовой пояс устройства установлен: " + timeZone);
        } catch (Exception e) {
            System.err.println("[TimeZone] Не удалось установить часовой пояс: " + e.getMessage());
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if ("local".equals(config.mobileProvider().toLowerCase())) {
            restoreSystemAnimations();
        }

        AndroidDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                if ("browserstack".equals(config.mobileProvider().toLowerCase())) {
                    String status = isClassFailed ? "failed" : "passed";
                    String reason = isClassFailed ? "One or more tests failed in this class" : "All tests passed";

                    driver.executeScript(
                            "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": " +
                                    "{\"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}"
                    );
                }
            } catch (Exception e) {
                System.err.println("[BrowserStack] Не удалось отправить статус сессии: " + e.getMessage());
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