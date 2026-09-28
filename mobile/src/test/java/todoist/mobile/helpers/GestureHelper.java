package todoist.mobile.helpers;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.time.Duration;
import java.util.Collections;

public class GestureHelper {

    public static void scrollUntilVisible(AppiumDriver driver, By locator, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            if (!driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed()) {
                return;
            }
            swipeUpToScroll(driver);
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "Элемент не обнаружен в контейнере меню после максимального числа свайпов: " + maxAttempts
        );
    }

    public static void swipeUpToScroll(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.80);
        int endY = (int) (size.getHeight() * 0.20);
        performSwipe(driver, startX, startY, startX, endY);
    }

    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = (int) (size.getHeight() * 0.45);
        int endY = (int) (size.getHeight() * 0.85);
        performSwipe(driver, startX, startY, startX, endY);
    }

    public static void swipeDownWithinElement(AppiumDriver driver, By containerLocator) {
        Rectangle rect = driver.findElement(containerLocator).getRect();
        int startX = rect.getX() + rect.getWidth() / 2;
        int startY = rect.getY() + (int) (rect.getHeight() * 0.25);
        int endY = rect.getY() + (int) (rect.getHeight() * 0.75);
        performSwipe(driver, startX, startY, startX, endY);
    }

    public static void swipeLeft(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startY = size.getHeight() / 2;
        int startX = (int) (size.getWidth() * 0.85);
        int endX = (int) (size.getWidth() * 0.15);
        performSwipe(driver, startX, startY, endX, startY);
    }

    private static void performSwipe(AppiumDriver driver, int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipe));
    }
}
