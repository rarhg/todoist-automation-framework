package todoist.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.api.models.ProjectRequest;
import todoist.api.models.ProjectResponse;
import todoist.api.steps.ProjectSteps;
import todoist.api.stubs.WireMockStubs;
import todoist.config.ConfigProvider;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление проектами")
@Feature("API: CRUD-операции с проектами")
@Tag("api")
@DisplayName("API: Операции с проектами Todoist")
public class ProjectTest extends BaseApiTest {

    private final ProjectSteps projectSteps = new ProjectSteps();

    @Test
    @Story("Создание проекта")
    @DisplayName("Успешное создание проекта")
    void shouldCreateProjectSuccessfully() {
        Faker faker = new Faker();
        String projectName = faker.commerce().department();
        String generatedId = faker.internet().uuid();

        ProjectRequest request = ProjectRequest.builder()
                .name(projectName)
                .isFavorite(true)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"blue\",\"is_favorite\":true,\"url\":\"https://todoist.com\"}",
                generatedId, projectName
        );

        WireMockStubs.stubPostSuccess(wireMockServer, "/projects", "$.name", projectName, mockResponseJson);

        ProjectResponse response = projectSteps.createProject(getMockBaseUrl(), request);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(projectName);
        assertThat(response.getIsFavorite()).isTrue();
    }

    @Test
    @Story("Получение проекта")
    @DisplayName("Успешное получение проекта по ID")
    void shouldGetProjectSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String projectName = faker.commerce().department();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"charcoal\",\"is_favorite\":false}",
                generatedId, projectName
        );

        WireMockStubs.stubGetSuccess(wireMockServer, String.format("/projects/%s", generatedId), mockResponseJson);

        ProjectResponse response = projectSteps.getProjectById(getMockBaseUrl(), generatedId);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(projectName);
    }

    @Test
    @Story("Удаление проекта")
    @DisplayName("Успешное удаление проекта")
    void shouldDeleteProjectSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubDeleteSuccess(wireMockServer, String.format("/projects/%s", generatedId));

        projectSteps.deleteProject(getMockBaseUrl(), generatedId);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание проекта без авторизационного токена")
    void shouldNotCreateProjectWithoutToken() {
        Faker faker = new Faker();
        ProjectRequest request = ProjectRequest.builder()
                .name(faker.commerce().department())
                .build();

        WireMockStubs.stubUnauthorizedMissingToken(wireMockServer, "POST", "/projects");

        var response = projectSteps.createProjectWithoutToken(getMockBaseUrl(), request);

        assertThat(response.getStatusCode()).isEqualTo(401);
        assertThat(response.jsonPath().getString("error")).isEqualTo("Unauthorized");
    }

    @Test
    @Story("Обновление проекта")
    @DisplayName("Успешное обновление проекта")
    void shouldUpdateProjectSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String updatedName = faker.commerce().department();

        ProjectRequest updateRequest = ProjectRequest.builder()
                .name(updatedName)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"blue\",\"is_favorite\":false}",
                generatedId, updatedName
        );

        WireMockStubs.stubUpdateSuccess(wireMockServer, "/projects/" + generatedId, "$.name", updatedName, mockResponseJson);

        ProjectResponse response = projectSteps.updateProject(getMockBaseUrl(), generatedId, updateRequest);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(updatedName);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание проекта с невалидным токеном авторизации")
    void shouldNotCreateProjectWithInvalidToken() {
        Faker faker = new Faker();
        ProjectRequest request = ProjectRequest.builder()
                .name(faker.commerce().department())
                .build();

        WireMockStubs.stubUnauthorizedInvalidToken(wireMockServer, "POST", "/projects",
                ConfigProvider.CONFIG.mockInvalidToken());

        var response = projectSteps.createProjectWithInvalidToken(getMockBaseUrl(), request);

        assertThat(response.getStatusCode()).isEqualTo(401);
        assertThat(response.jsonPath().getString("error")).isEqualTo("Unauthorized");
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание проекта с пустым именем (400 Bad Request)")
    void shouldNotCreateProjectWithEmptyName() {
        ProjectRequest request = ProjectRequest.builder()
                .name("")
                .build();

        WireMockStubs.stubBadRequest(wireMockServer, "POST", "/projects", "$.name", "", "{\"error\": \"Name is required\"}");

        var response = projectSteps.createProjectInvalid(getMockBaseUrl(), request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("error")).isEqualTo("Name is required");
    }
}