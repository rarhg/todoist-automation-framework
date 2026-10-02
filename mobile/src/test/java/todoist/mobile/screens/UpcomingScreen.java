package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UpcomingScreen extends BaseMobileScreen {

    private static final Locale RU = Locale.forLanguageTag("ru");

    public UpcomingScreen(AppiumDriver driver) {
        super(driver);
    }

    public UpcomingScreen selectDayInCalendar(int daysToAdd) {
        String title = dayHeaderTitle(LocalDate.now().plusDays(daysToAdd));
        log.info("Ищем элемент заголовка дня по структуре: {}", title);
        click(dayHeaderLocator(title));
        return this;
    }

    public boolean isHeaderForDateDisplayed(LocalDate date) {
        String title = dayHeaderTitle(date);
        boolean displayed = isDisplayedWithWait(dayHeaderLocator(title), Duration.ofSeconds(10));
        if (!displayed) {
            log.warn("Не смогли найти заголовок даты: {}", title);
        }
        return displayed;
    }

    private static String dayHeaderTitle(LocalDate date) {
        String dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE", RU));
        dayOfWeek = dayOfWeek.substring(0, 1).toUpperCase(RU) + dayOfWeek.substring(1);
        String month = date.format(DateTimeFormatter.ofPattern("MMM", RU)).toLowerCase(RU).replace(".", "") + ".";
        return String.format("%s, %d %s", dayOfWeek, date.getDayOfMonth(), month);
    }

    private static By dayHeaderLocator(String title) {
        return AppiumBy.androidUIAutomator(String.format(
                "new UiSelector().resourceId(\"android:id/list\")" +
                        ".childSelector(new UiSelector().resourceId(\"com.todoist:id/container\")" +
                        ".childSelector(new UiSelector().resourceId(\"android:id/title\").text(\"%s\")))",
                title
        ));
    }
}
