package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import todoist.mobile.helpers.GestureHelper;
import todoist.mobile.screens.components.AddTaskModal;

import java.time.Duration;

public class MainScreen extends BaseMobileScreen {

    private final By inboxTitle = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Входящие\").instance(0)"
    );
    private final By fabAddTaskButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/fab\")"
    );
    private final By browseTabButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"test_tag_navigation\")"
    );
    private final By upcomingTabButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"test_tag_upcoming\")"
    );
    private final By inboxTabButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"test_tag_inbox\")"
    );

    private final By layoutMenuButton = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/menu_content_view_options\")"
    );
    private final By boardLayoutOption = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Доска\")" +
                    ".fromParent(new UiSelector().className(\"android.widget.RadioButton\"))"
    );
    private final By boardLayoutOptionText = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Доска\")"
    );
    private final By listLayoutOption = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Список\")" +
                    ".fromParent(new UiSelector().className(\"android.widget.RadioButton\"))"
    );
    private final By submitLayoutMenuButton = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"Сохранить|Готово|Save|Done\")"
    );

    private final By boardViewContainerLocator = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/board_view\")"
    );
    private final By boardColumnTitle = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/board_view\")" +
                    ".childSelector(new UiSelector().resourceId(\"android:id/title\"))"
    );

    private final By notificationAllowBanner = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Разрешить\")"
    );

    public MainScreen(AppiumDriver driver) {
        super(driver);
    }

    private By taskByText(String taskName) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + taskName + "\")");
    }

    private By getTaskCheckboxLocator(String taskName) {
        return AppiumBy.androidUIAutomator(String.format(
                "new UiSelector().resourceId(\"com.todoist:id/root\")" +
                        ".childSelector(new UiSelector().text(\"%s\"))" +
                        ".fromParent(new UiSelector().resourceId(\"com.todoist:id/checkmark\"))",
                taskName
        ));
    }

    public boolean isInboxPageDisplayed() {
        boolean displayed = isDisplayedWithWait(inboxTitle, Duration.ofSeconds(10));
        if (!displayed) {
            log.warn("Страница 'Входящие' не прогрузилась");
        }
        return displayed;
    }

    public AddTaskModal clickAddTaskFab() {
        click(fabAddTaskButton);
        return new AddTaskModal(driver);
    }

    public BrowseScreen openBrowseTab() {
        click(browseTabButton);
        return new BrowseScreen(driver);
    }

    public UpcomingScreen openUpcomingTab() {
        click(upcomingTabButton);
        return new UpcomingScreen(driver);
    }

    public MainScreen openInboxTab() {
        click(inboxTabButton);
        waitForVisibility(inboxTitle);
        return this;
    }

    public MainScreen openLayoutMenu() {
        log.info("Открытие панели настройки отображения");
        click(layoutMenuButton);
        return this;
    }

    public MainScreen selectBoardLayout() {
        log.info("Выбор опции 'Доска'");
        click(boardLayoutOptionText);
        try {
            log.debug("Доска checked={}", driver.findElement(boardLayoutOption).getAttribute("checked"));
        } catch (Exception e) {
            log.debug("Не удалось прочитать состояние 'Доска': {}", e.getMessage());
        }
        return this;
    }

    public MainScreen selectListLayout() {
        log.info("Выбор опции 'Список'");
        click(listLayoutOption);
        return this;
    }

    public MainScreen saveLayout() {
        log.info("Сохранение настроек отображения");
        click(submitLayoutMenuButton);
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(submitLayoutMenuButton));
        } catch (Exception e) {
            log.warn("Шторка не закрылась после «Сохранить»");
        }
        return this;
    }

    public MainScreen changeLayoutToBoard() {
        return openLayoutMenu().selectBoardLayout().saveLayout();
    }

    public MainScreen changeLayoutToList() {
        return openLayoutMenu().selectListLayout().saveLayout();
    }

    public MainScreen swipeToNextBoardColumn() {
        log.info("Горизонтальный свайп к следующей колонке внутри board_view");
        GestureHelper.swipeLeft(driver);
        return this;
    }

    public boolean isBoardLayoutActive() {
        boolean active = isDisplayedWithWait(boardViewContainerLocator, Duration.ofSeconds(10));
        if (!active) {
            log.warn("Контейнер board_view не обнаружен");
        }
        return active;
    }

    public boolean isBoardLayoutActiveNow() {
        return isDisplayedWithWait(boardViewContainerLocator, Duration.ofSeconds(2));
    }

    public String getCurrentBoardColumnTitle() {
        return waitForVisibility(boardColumnTitle).getText();
    }

    public boolean isNextBoardColumnVisible(String previousTitle) {
        try {
            String currentTitle = waitForVisibility(boardColumnTitle).getText();
            boolean changed = !currentTitle.equals(previousTitle);
            if (!changed) {
                log.warn("Заголовок колонки не изменился после свайпа: {}", currentTitle);
            }
            return changed;
        } catch (Exception e) {
            log.warn("Не удалось получить заголовок колонки после свайпа: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTaskNotVisible(String taskName) {
        return !isDisplayed(taskByText(taskName));
    }

    public boolean isTaskVisibleOnUi(String taskName) {
        return isDisplayedWithWait(taskByText(taskName), Duration.ofSeconds(10));
    }

    public MainScreen swipeDownToRefresh() {
        GestureHelper.swipeDown(driver);
        return this;
    }

    public MainScreen completeTaskByName(String taskName) {
        click(getTaskCheckboxLocator(taskName));
        return this;
    }

    public MainScreen dismissNotificationBannerIfPresent() {
        click(notificationAllowBanner, Duration.ofSeconds(3), false);
        return this;
    }
}
