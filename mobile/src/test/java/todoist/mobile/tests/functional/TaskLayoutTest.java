package todoist.mobile.tests.functional;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.api.models.ProjectResponse;
import todoist.api.models.SectionRequest;
import todoist.api.models.SectionResponse;
import todoist.api.steps.ProjectSteps;
import todoist.api.steps.SectionSteps;
import todoist.mobile.screens.MainScreen;
import todoist.mobile.tests.base.BaseFunctionalTest;

import java.util.Arrays;
import java.util.List;

import static todoist.api.specs.Specs.PRODUCTION_REQUEST_SPEC;

@Epic("Управление задачами")
@Feature("Mobile: Режимы отображения (Список/Доска)")
@Tag("androidLocal")
public class TaskLayoutTest extends BaseFunctionalTest {

    private final ProjectSteps projectSteps = new ProjectSteps();
    private final SectionSteps sectionSteps = new SectionSteps();

    private static final String SECTION_PREFIX = "Автотест раздел";

    private void deleteAutotestSections() {
        try {
            String inboxId = findInboxProjectId();
            for (SectionResponse s : sectionSteps.getSectionsByProject(inboxId, PRODUCTION_REQUEST_SPEC)) {
                if (s.getName() != null && s.getName().startsWith(SECTION_PREFIX)) {
                    try {
                        sectionSteps.deleteSection(s.getId(), PRODUCTION_REQUEST_SPEC);
                    } catch (Exception e) {
                        log.warn("Не удалось удалить раздел {}: {}", s.getId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка очистки разделов: {}", e.getMessage());
        }
    }

    @BeforeEach
    void ensureInboxHasMultipleSections() {
        log.info("Очистка старых разделов и подготовка двух новых в Inbox");

        deleteAutotestSections();

        String inboxProjectId = findInboxProjectId();

        for (String sectionName : List.of(SECTION_PREFIX + " A", SECTION_PREFIX + " B")) {
            SectionRequest request = SectionRequest.builder()
                    .projectId(inboxProjectId)
                    .name(sectionName)
                    .build();
            SectionResponse created = sectionSteps.createSection(request, PRODUCTION_REQUEST_SPEC);
            log.info("Создан раздел с ID: {}", created.getId());
        }
    }

    private String findInboxProjectId() {
        ProjectResponse[] projects = projectSteps.getAllProjects(PRODUCTION_REQUEST_SPEC);
        return Arrays.stream(projects)
                .filter(p -> Boolean.TRUE.equals(p.getIsInboxProject()))
                .findFirst()
                .map(ProjectResponse::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Не удалось найти Inbox-проект через API — проверьте поле is_inbox_project в ответе"));
    }

    @Test
    @Story("Переключение Список ↔ Доска")
    @DisplayName("Переключение раскладки со Списка на Доску")
    void switchListToBoardLayoutTest() {
        MainScreen mainScreen = new MainScreen(driver());

        Assertions.assertTrue(mainScreen.isInboxPageDisplayed(), "Не открыта страница «Входящие»");

        mainScreen.openLayoutMenu()
                .selectBoardLayout()
                .saveLayout();

        Assertions.assertTrue(
                mainScreen.isBoardLayoutActive(),
                "Экран не в режиме Доски (board_view не найден)!"
        );
    }

    @Test
    @Story("Навигация между колонками Доски")
    @DisplayName("Горизонтальный свайп между колонками в режиме Доски")
    void horizontalSwipeBetweenBoardColumnsTest() {
        MainScreen mainScreen = new MainScreen(driver());
        mainScreen.swipeDownToRefresh();

        mainScreen.changeLayoutToBoard();
        String previousTitle = mainScreen.getCurrentBoardColumnTitle();
        mainScreen.swipeToNextBoardColumn();

        Assertions.assertTrue(
                mainScreen.isNextBoardColumnVisible(previousTitle),
                "После горизонтального свайпа заголовок колонки не изменился!"
        );
    }

    @AfterEach
    void cleanUpAndResetLayout() {
        log.info("Сброс отображения в Список для изоляции следующих тестов");
        try {
            MainScreen mainScreen = new MainScreen(driver());
            if (mainScreen.isBoardLayoutActiveNow()) {
                mainScreen.changeLayoutToList();
            }
        } catch (Exception e) {
            log.error("Ошибка сброса раскладки: {}", e.getMessage());
        }

        log.info("Удаление всех тестовых разделов из Inbox");
        deleteAutotestSections();
    }
}
