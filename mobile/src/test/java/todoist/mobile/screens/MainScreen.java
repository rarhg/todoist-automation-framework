package todoist.mobile.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
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

    private final By emptyStateIllustration = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/placeholder\")"
    );
    private final By emptyStateText = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/empty_title\")"
    );
    private final By emptyStateSubText = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/empty_text\")"
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

    public MainScreen(AppiumDriver driver) {
        super(driver);
    }

    private By getTaskCheckboxLocator(String taskName) {
        return AppiumBy.androidUIAutomator(String.format(
                "new UiSelector().resourceId(\"com.todoist:id/root\")" +
                        ".childSelector(new UiSelector().text(\"%s\"))" +
                        ".fromParent(new UiSelector().resourceId(\"com.todoist:id/checkmark\"))",
                taskName
        ));
    }

    public void waitForRenderAndSubmitLayout() {
        System.out.println("[Layout] Ожидание рендеринга и кликабельности кнопки сохранения шторки...");
        waitForClickability(submitLayoutMenuButton);
        click(submitLayoutMenuButton);
        System.out.println("[Layout] Клик по кнопке сохранения успешно выполнен.");
    }

    public boolean isInboxPageDisplayed() {
        try {
            waitForVisibility(inboxTitle);
            return isDisplayed(inboxTitle);
        } catch (Exception e) {
            System.err.println("[Error] Страница Входящие не прогрузилась: " + e.getMessage());
            return false;
        }
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
        System.out.println("[Layout] Открытие панели настройки Отображения...");
        click(layoutMenuButton);
        return this;
    }

    public MainScreen selectBoardLayout() {
        System.out.println("[Layout] Выбор опции 'Доска'...");
        click(boardLayoutOptionText);
        try {
            String checked = driver.findElement(boardLayoutOption).getAttribute("checked");
            System.out.println("[Layout][Diag] Доска checked=" + checked);
        } catch (Exception e) {
            System.err.println("[Layout][Diag] Не удалось прочитать состояние 'Доска': " + e.getMessage());
        }
        return this;
    }

    public MainScreen saveLayout() {
        waitForRenderAndSubmitLayout();
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .invisibilityOfElementLocated(submitLayoutMenuButton));
            System.out.println("[Layout][Diag] Шторка закрылась после Сохранить");
        } catch (Exception e) {
            System.err.println("[Layout][Diag] Шторка НЕ закрылась после Сохранить");
        }
        return this;
    }

    public MainScreen changeLayoutToBoard() {
        return openLayoutMenu().selectBoardLayout().saveLayout();
    }

    public MainScreen changeLayoutToList() {
        System.out.println("[Layout] Открытие панели настройки Отображения...");
        click(layoutMenuButton);

        System.out.println("[Layout] Выбор опции 'Список'...");
        click(listLayoutOption);

        waitForRenderAndSubmitLayout();
        return this;
    }

    public MainScreen swipeToNextBoardColumn() {
        System.out.println("[Gestures] Горизонтальный свайп к следующей колонке внутри board_view...");
        GestureHelper.swipeLeft(driver);
        return this;
    }

    public boolean isBoardLayoutActive() {
        try {
            return waitForVisibility(boardViewContainerLocator).isDisplayed();
        } catch (Exception e) {
            System.err.println("[Validation] Контейнер board_view не обнаружен: " + e.getMessage());
            return false;
        }
    }

    public boolean isBoardLayoutActiveNow() {
        try {
            return waitForVisibility(boardViewContainerLocator, Duration.ofSeconds(2)).isDisplayed();
        } catch (Exception e) {
            try {
                return isDisplayed(boardViewContainerLocator);
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private final By boardColumnTitle = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.todoist:id/board_view\")" +
                    ".childSelector(new UiSelector().resourceId(\"android:id/title\"))"
    );

    public String getCurrentBoardColumnTitle() {
        return waitForVisibility(boardColumnTitle).getText();
    }

    public boolean isNextBoardColumnVisible(String previousTitle) {
        try {
            String currentTitle = waitForVisibility(boardColumnTitle).getText();
            boolean changed = !currentTitle.equals(previousTitle);
            if (!changed) {
                System.err.println("[Validation] Заголовок колонки не изменился после свайпа: " + currentTitle);
            }
            return changed;
        } catch (Exception e) {
            System.err.println("[Validation] Не удалось получить заголовок колонки после свайпа: " + e.getMessage());
            return false;
        }
    }

    public boolean isTaskNotVisible(String taskName) {
        By taskLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + taskName + "\")");
        return !isDisplayed(taskLocator);
    }

    public MainScreen swipeDownToRefresh() {
        GestureHelper.swipeDown(driver);
        return this;
    }

    public boolean isEmptyStateIllustrationDisplayed() {
        return isDisplayed(emptyStateIllustration);
    }

    public String getEmptyStateText() {
        return waitForVisibility(emptyStateText).getText();
    }

    public MainScreen completeTaskByName(String taskName) {
        By taskCheckbox = getTaskCheckboxLocator(taskName);
        click(taskCheckbox);
        return this;
    }

    public boolean isTaskVisibleOnUi(String taskName) {
        By dynamicTaskLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + taskName + "\")");
        try {
            return waitForVisibility(dynamicTaskLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private final By notificationAllowBanner = AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Разрешить\")"
    );

    public MainScreen dismissNotificationBannerIfPresent() {
        click(notificationAllowBanner, Duration.ofSeconds(3), false);
        return this;
    }


}