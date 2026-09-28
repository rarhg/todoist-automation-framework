package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseMobileScreen {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_STABILIZE_TIMEOUT = Duration.ofSeconds(5);

    protected final AppiumDriver driver;
    protected final WebDriverWait wait;

    private final By proPromoBottomSheet = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/design_bottom_sheet\")"
    );

    protected BaseMobileScreen(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    protected WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, DEFAULT_TIMEOUT);
    }

    protected WebElement waitForVisibility(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickability(By locator) {
        return waitForClickability(locator, DEFAULT_TIMEOUT);
    }

    protected WebElement waitForClickability(By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        click(locator, DEFAULT_TIMEOUT, true);
    }

    protected boolean click(By locator, Duration timeout, boolean required) {
        try {
            waitForElementToStabilize(locator, timeout);
            WebElement element = waitForClickability(locator, timeout);
            element.click();
            return true;
        } catch (RuntimeException e) {
            if (required) {
                throw e;
            }
            System.err.println("[Click] Мягкий клик не выполнен (необязательный элемент): "
                    + locator + " -> " + e.getMessage());
            return false;
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForElementToStabilize(locator);
        waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    public boolean isDisplayed(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected WebElement waitForElementToStabilize(By locator) {
        return waitForElementToStabilize(locator, DEFAULT_STABILIZE_TIMEOUT);
    }

    protected WebElement waitForElementToStabilize(By locator, Duration timeout) {
        WebElement element = waitForVisibility(locator, timeout);
        Point lastLocation = null;
        long maxWaitTime = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < maxWaitTime) {
            Point currentLocation = element.getLocation();

            if (lastLocation != null && currentLocation.equals(lastLocation)) {
                System.out.println("[Engine] Элемент стабилизировался в точке: " + currentLocation);
                break;
            }

            lastLocation = currentLocation;

            try {
                new WebDriverWait(driver, Duration.ofMillis(150)).until(d -> false);
            } catch (TimeoutException ignored) {
            }
        }
        return element;
    }

    protected boolean isDisplayedWithWait(By locator, Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void dismissProPromoIfPresent() {
        if (isDisplayedWithWait(proPromoBottomSheet, Duration.ofSeconds(3))) {
            System.out.println("[Promo] Обнаружен баннер 'Попробуйте Pro бесплатно', закрываем через системный Back...");
            driver.navigate().back();
            waitUntilPromoGone();
        }
    }

    private void waitUntilPromoGone() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(proPromoBottomSheet));
        } catch (Exception ignored) {
        }
    }
}