package todoist.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import todoist.api.models.SectionRequest;
import todoist.api.models.SectionResponse;
import todoist.api.steps.SectionSteps;
import todoist.api.stubs.WireMockStubs;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление разделами")
@Feature("API: CRUD-операции с разделами")
@Tag("api")
@DisplayName("API: Операции со секциями Todoist")
@Execution(ExecutionMode.CONCURRENT)
public class SectionTest extends BaseApiTest {

    private final SectionSteps sectionSteps = new SectionSteps();

    @Test
    @Story("Создание раздела")
    @DisplayName("Успешное создание секции в проекте")
    void shouldCreateSectionSuccessfully() {
        Faker faker = new Faker();
        String sectionName = faker.company().industry();
        String projectId = faker.internet().uuid();
        String generatedId = faker.internet().uuid();

        SectionRequest request = SectionRequest.builder()
                .projectId(projectId)
                .name(sectionName)
                .order(1)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"project_id\":\"%s\",\"name\":\"%s\",\"order\":1}",
                generatedId, projectId, sectionName
        );

        WireMockStubs.stubPostSuccess(wireMockServer, "/sections", "$.name", sectionName, mockResponseJson);

        SectionResponse response = sectionSteps.createSection(getMockBaseUrl(), request);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getProjectId()).isEqualTo(projectId);
        assertThat(response.getName()).isEqualTo(sectionName);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание секции без имени (400 Bad Request)")
    void shouldNotCreateSectionWithoutName() {
        Faker faker = new Faker();
        String projectId = faker.internet().uuid();

        SectionRequest request = SectionRequest.builder()
                .projectId(projectId)
                .build();

        WireMockStubs.stubBadRequest(wireMockServer, "POST", "/sections", "$.project_id", projectId,
                "{\"error\": \"Name is required\"}");

        var response = sectionSteps.createSectionInvalid(getMockBaseUrl(), request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("error")).isEqualTo("Name is required");
    }

    @Test
    @Story("Обновление раздела")
    @DisplayName("Успешное обновление секции")
    void shouldUpdateSectionSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String projectId = faker.internet().uuid();
        String updatedName = faker.company().industry();

        SectionRequest updateRequest = SectionRequest.builder()
                .name(updatedName)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"project_id\":\"%s\",\"name\":\"%s\",\"order\":1}",
                generatedId, projectId, updatedName
        );

        WireMockStubs.stubUpdateSuccess(wireMockServer, "/sections/" + generatedId, "$.name", updatedName, mockResponseJson);

        SectionResponse response = sectionSteps.updateSection(getMockBaseUrl(), generatedId, updateRequest);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(updatedName);
    }

    @Test
    @Story("Удаление раздела")
    @DisplayName("Успешное удаление секции")
    void shouldDeleteSectionSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubDeleteSuccess(wireMockServer, "/sections/" + generatedId);

        sectionSteps.deleteSection(getMockBaseUrl(), generatedId);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Получение секции по несуществующему ID (404 Not Found)")
    void shouldReturn404WhenSectionNotFound() {
        String nonExistentId = "999-invalid-id";

        WireMockStubs.stubNotFound(wireMockServer, "GET", String.format("/sections/%s", nonExistentId));

        var response = sectionSteps.getSectionById(getMockBaseUrl(), nonExistentId, 404);

        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.body().asString()).contains("Resource not found");
    }
}