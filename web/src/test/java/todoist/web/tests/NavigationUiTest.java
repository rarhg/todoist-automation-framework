package todoist.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import todoist.web.pages.InboxPage;
import todoist.web.pages.TodayPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Epic("Управление задачами")
@Feature("Web: Навигация по спискам задач")
@Tag("web")
@DisplayName("Web: Навигация по спискам задач")
public class NavigationUiTest extends BaseWebTest {

    private final InboxPage inboxPage = new InboxPage();
    private final TodayPage todayPage = new TodayPage();

    @ParameterizedTest
    @Story("Переключение между списками (Входящие/Сегодня)")
    @ValueSource(strings = {"Входящие", "Сегодня"})
    @DisplayName("Параметризованный тест: Проверка переключения разделов сайдбара")
    public void shouldNavigateToCorrectSection(String targetSection) {
        inboxPage.openPage("/app/inbox");

        if (targetSection.equals("Входящие")) {
            inboxPage.clickInboxLink();
            inboxPage.verifyPageOpened();
        } else if (targetSection.equals("Сегодня")) {
            $("#filter_today a").shouldBe(visible).click();
            todayPage.verifyPageIsOpened();
        }
    }
}