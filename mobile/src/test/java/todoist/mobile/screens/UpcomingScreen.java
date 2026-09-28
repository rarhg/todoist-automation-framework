package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UpcomingScreen extends BaseMobileScreen {

    public UpcomingScreen(AppiumDriver driver) {
        super(driver);
    }

    public UpcomingScreen selectDayInCalendar(int daysToAdd) {
        LocalDate targetDate = LocalDate.now().plusDays(daysToAdd);

        String dayOfWeek = targetDate.format(DateTimeFormatter.ofPattern("EEEE", new Locale("ru")));
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        int dayOfMonth = targetDate.getDayOfMonth();
        String month = targetDate.format(DateTimeFormatter.ofPattern("MMM", new Locale("ru"))).toLowerCase();
        month = month.replace(".", "") + ".";

        String expectedHeaderTitle = String.format("%s, %d %s", dayOfWeek, dayOfMonth, month);
        System.out.println("[Calendar] Ищем элемент заголовка дня по структуре: " + expectedHeaderTitle);

        String safeUiAutomator = String.format(
                "new UiSelector().resourceId(\"android:id/list\")" +
                        ".childSelector(new UiSelector().resourceId(\"com.todoist:id/container\")" +
                        ".childSelector(new UiSelector().resourceId(\"android:id/title\").text(\"%s\")))",
                expectedHeaderTitle
        );

        By dynamicDayLocator = AppiumBy.androidUIAutomator(safeUiAutomator);

        click(dynamicDayLocator);
        return this;
    }

    public boolean isHeaderForDateDisplayed(LocalDate date) {
        String dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", new Locale("ru")));
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);

        int dayOfMonth = date.getDayOfMonth();
        String month = date.format(DateTimeFormatter.ofPattern("MMM", new Locale("ru"))).toLowerCase();
        month = month.replace(".", "") + ".";

        String expectedHeaderTitle = String.format("%s, %d %s", dayOfWeek, dayOfMonth, month);

        String safeUiAutomator = String.format(
                "new UiSelector().resourceId(\"android:id/list\")" +
                        ".childSelector(new UiSelector().resourceId(\"com.todoist:id/container\")" +
                        ".childSelector(new UiSelector().resourceId(\"android:id/title\").text(\"%s\")))",
                expectedHeaderTitle
        );

        By targetDayHeader = AppiumBy.androidUIAutomator(safeUiAutomator);

        try {
            return waitForVisibility(targetDayHeader).isDisplayed();
        } catch (Exception e) {
            System.err.println("[Error] Не смогли найти заголовок даты: " + expectedHeaderTitle);
            return false;
        }
    }
}
