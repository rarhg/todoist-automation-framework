package todoist.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import todoist.api.models.ProjectResponse;
import todoist.api.steps.ProjectSteps;
import todoist.web.components.ProjectSidebar;
import todoist.web.pages.InboxPage;
import todoist.web.pages.ProjectPage;
import todoist.web.pages.ProjectsListPage;

import java.util.Arrays;
import java.util.UUID;

import static todoist.api.specs.Specs.PRODUCTION_REQUEST_SPEC;

@Epic("Управление проектами")
@Feature("Web: Управление проектами через UI")
@Tag("web")
@DisplayName("Web: Тесты на управление проектами через UI")
public class ProjectUiTest extends BaseWebTest {

    private final ProjectPage projectPage = new ProjectPage();
    private final ProjectSteps projectSteps = new ProjectSteps();
    private final InboxPage inboxPage = new InboxPage();
    private final ProjectSidebar projectSidebar = new ProjectSidebar();
    private final ProjectsListPage projectsListPage = new ProjectsListPage();
    private final ThreadLocal<String> uniqueProjectName = new ThreadLocal<>();

    private static final Logger log = LoggerFactory.getLogger(ProjectUiTest.class);
    @BeforeEach
    void setUpSession() {
        uniqueProjectName.set("UI_PROJ_" + UUID.randomUUID().toString().substring(0, 8));
    }

    @Test
    @Story("Создание проекта")
    @DisplayName("Успешное создание нового проекта через UI")
    void shouldCreateProjectViaUi() {
        String currentProjectName = uniqueProjectName.get();
        inboxPage.openPage("/app/inbox");

        inboxPage.verifyPageOpened();
        projectSidebar.initiateNewProjectCreation();

        projectsListPage.enterProjectName(currentProjectName)
                .enterProjectDescription("Описание автоматического проекта")
                .submitProjectCreation();

        projectPage.verifyProjectHeader(currentProjectName);
    }

    @AfterEach
    void cleanUpData() {
        try {
            String currentProjectName = uniqueProjectName.get();
            if (currentProjectName != null) {
                ProjectResponse[] projects = projectSteps.getAllProjects(PRODUCTION_REQUEST_SPEC);
                Arrays.stream(projects)
                        .filter(p -> currentProjectName.equals(p.getName()))
                        .findFirst()
                        .ifPresent(p -> projectSteps.deleteProject(p.getId(), PRODUCTION_REQUEST_SPEC));
            }
        } catch (Exception e) {
            log.error("Не удалось удалить проект после теста: {}", e.getMessage());
        } finally {
            uniqueProjectName.remove();
        }
    }
}