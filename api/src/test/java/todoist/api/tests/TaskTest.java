package todoist.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import todoist.api.models.TaskRequest;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskApiSteps;
import todoist.api.stubs.WireMockStubs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Tag("api")
@Epic("Управление задачами")
@Feature("API: CRUD-операции с задачами")
@DisplayName("API: Операции с задачами Todoist")
@Execution(CONCURRENT)
public class TaskTest extends BaseApiTest {

    private final TaskApiSteps taskSteps = new TaskApiSteps();

    @Test
    @Story("Создание задачи")
    @DisplayName("Успешное создание задачи с валидацией контракта")
    void shouldCreateTaskSuccessfully() {
        Faker faker = new Faker();
        String taskName = faker.book().title();
        String generatedId = faker.internet().uuid();

        TaskRequest requestBody = TaskRequest.builder()
                .content(taskName)
                .description("Task created by autotest")
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"content\":\"%s\",\"description\":\"Task created by autotest\",\"project_id\":null,\"checked\":false}",
                generatedId, taskName
        );

        WireMockStubs.stubPostSuccess(wireMockServer, "/tasks", "$.content", taskName, mockResponseJson);

        TaskResponse response = taskSteps.createTaskWithContractCheck(getMockBaseUrl(), requestBody);

        assertThat(response.getId()).isNotEmpty();
        assertThat(response.getContent()).isEqualTo(taskName);
        assertThat(response.getChecked()).isFalse();
    }

    @Test
    @Story("Получение задачи")
    @DisplayName("Успешное получение задачи по ID")
    void shouldGetTaskByIdSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String taskName = faker.job().title();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"content\":\"%s\",\"checked\":false}",
                generatedId, taskName
        );

        WireMockStubs.stubGetSuccess(wireMockServer, String.format("/tasks/%s", generatedId), mockResponseJson);

        TaskResponse response = taskSteps.getTaskById(getMockBaseUrl(), generatedId);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getContent()).isEqualTo(taskName);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Получение задачи по невалидному ID (404 Not Found)")
    void shouldReturn404WhenTaskNotFound() {
        String invalidId = "invalid-task-id-999";

        WireMockStubs.stubNotFound(wireMockServer, "GET", String.format("/tasks/%s", invalidId));

        var response = taskSteps.getTaskByInvalidId(getMockBaseUrl(), invalidId, 404);

        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.body().asString()).contains("Resource not found");
    }

    @Test
    @Story("Выполнение задачи")
    @DisplayName("Успешное закрытие (выполнение) задачи")
    void shouldCloseTaskSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubPostNoContent(wireMockServer, "/tasks/" + generatedId + "/close");

        taskSteps.closeTask(getMockBaseUrl(), generatedId);
    }

    @Test
    @Story("Обновление задачи")
    @DisplayName("Успешное обновление задачи (изменение priority/содержимого)")
    void shouldUpdateTaskSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String updatedContent = faker.book().title();

        TaskRequest updateRequest = TaskRequest.builder()
                .content(updatedContent)
                .priority(4)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"content\":\"%s\",\"priority\":4,\"checked\":false}",
                generatedId, updatedContent
        );

        WireMockStubs.stubUpdateSuccess(wireMockServer, "/tasks/" + generatedId, "$.content", updatedContent, mockResponseJson);

        TaskResponse response = taskSteps.updateTask(getMockBaseUrl(), generatedId, updateRequest);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getContent()).isEqualTo(updatedContent);
        assertThat(response.getPriority()).isEqualTo(4);
    }

    @Test
    @Story("Удаление задачи")
    @DisplayName("Успешное удаление задачи")
    void shouldDeleteTaskSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubDeleteSuccess(wireMockServer, "/tasks/" + generatedId);

        taskSteps.deleteTask(getMockBaseUrl(), generatedId);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание задачи с невалидным projectId (400 Bad Request)")
    void shouldNotCreateTaskWithInvalidProjectId() {
        Faker faker = new Faker();
        TaskRequest request = TaskRequest.builder()
                .content(faker.book().title())
                .projectId("invalid-project-id-999")
                .build();

        WireMockStubs.stubBadRequest(wireMockServer, "POST", "/tasks", "$.project_id", "invalid-project-id-999",
                "{\"error\": \"Invalid project_id\"}");

        var response = taskSteps.createTaskInvalid(getMockBaseUrl(), request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("error")).isEqualTo("Invalid project_id");
    }
}