package todoist.mobile.tests.functional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.screens.UpcomingScreen;
import todoist.mobile.tests.base.BaseFunctionalTest;

import java.time.LocalDate;

@Epic("Управление задачами")
@Feature("Mobile: Календарь и планирование задач")
@Tag("androidLocal")
@Story("Выбор даты в календаре")
public class TaskCalendarTest extends BaseFunctionalTest {

    @Test
    @DisplayName("Динамический выбор даты в календаре-ленте")
    void dynamicCalendarDateSelectionTest() {
        MainScreen mainScreen = new MainScreen(driver());
        UpcomingScreen upcomingScreen = mainScreen.openUpcomingTab();

        int daysToAdd = 2;
        LocalDate targetDate = LocalDate.now().plusDays(daysToAdd);

        upcomingScreen.selectDayInCalendar(daysToAdd);

        Assertions.assertTrue(
                upcomingScreen.isHeaderForDateDisplayed(targetDate),
                String.format("Фокус списка задач не переключился на выбранную дату (%s)!", targetDate)
        );
    }

    @AfterEach
    void returnToInboxTab() {
        log.info("Возвращаемся на вкладку 'Входящие' для изоляции следующих тестов");
        try {
            new MainScreen(driver()).openInboxTab();
        } catch (Exception e) {
            log.warn("Не удалось вернуться на 'Входящие': {}", e.getMessage());
        }
    }
}
