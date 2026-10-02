package todoist.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import todoist.api.models.CommentRequest;
import todoist.api.models.CommentResponse;
import todoist.api.steps.CommentSteps;
import todoist.api.stubs.WireMockStubs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@Epic("Комментарии")
@Feature("API: CRUD-операции с комментариями")
@Tag("api")
@DisplayName("API: Операции с комментариями Todoist")
@Execution(CONCURRENT)
public class CommentTest extends BaseApiTest {

    private final CommentSteps commentSteps = new CommentSteps();

    @Test
    @Story("Создание комментария")
    @DisplayName("Успешное добавление комментария к задаче")
    void shouldCreateCommentSuccessfully() {
        Faker faker = new Faker();
        String commentContent = faker.lorem().sentence();
        String taskId = faker.internet().uuid();
        String generatedId = faker.internet().uuid();

        CommentRequest request = CommentRequest.builder()
                .taskId(taskId)
                .content(commentContent)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"item_id\":\"%s\",\"content\":\"%s\",\"posted_at\":\"2026-07-27T12:00:00Z\"}",
                generatedId, taskId, commentContent
        );

        WireMockStubs.stubPostSuccess(wireMockServer, "/comments", "$.content", commentContent, mockResponseJson);

        CommentResponse response = commentSteps.createComment(getMockBaseUrl(), request);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getTaskId()).isEqualTo(taskId);
        assertThat(response.getContent()).isEqualTo(commentContent);
    }

    @Test
    @Story("Получение комментария")
    @DisplayName("Успешное получение комментария по ID")
    void shouldGetCommentByIdSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String taskId = faker.internet().uuid();
        String content = faker.lorem().sentence();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"item_id\":\"%s\",\"content\":\"%s\",\"posted_at\":\"2026-07-27T12:00:00Z\"}",
                generatedId, taskId, content
        );

        WireMockStubs.stubGetSuccess(wireMockServer, "/comments/" + generatedId, mockResponseJson);

        CommentResponse response = commentSteps.getCommentById(getMockBaseUrl(), generatedId);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getContent()).isEqualTo(content);
    }

    @Test
    @Story("Получение списка комментариев")
    @DisplayName("Успешное получение списка комментариев по задаче")
    void shouldGetAllCommentsSuccessfully() {
        Faker faker = new Faker();
        String taskId = faker.internet().uuid();
        String commentId = faker.internet().uuid();

        String mockResponseJson = String.format(
                "{\"results\":[{\"id\":\"%s\",\"item_id\":\"%s\",\"content\":\"note\",\"posted_at\":\"2026-07-27T12:00:00Z\"}],\"next_cursor\":null}",
                commentId, taskId
        );

        WireMockStubs.stubGetSuccess(wireMockServer, "/comments?task_id=" + taskId, mockResponseJson);

        CommentResponse[] response = commentSteps.getAllComments(getMockBaseUrl(), taskId);

        assertThat(response).hasSize(1);
        assertThat(response[0].getTaskId()).isEqualTo(taskId);
    }

    @Test
    @Story("Обновление комментария")
    @DisplayName("Успешное обновление комментария")
    void shouldUpdateCommentSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String updatedContent = faker.lorem().sentence();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"item_id\":null,\"content\":\"%s\",\"posted_at\":\"2026-07-27T12:00:00Z\"}",
                generatedId, updatedContent
        );

        WireMockStubs.stubUpdateSuccess(wireMockServer, "/comments/" + generatedId, "$.content", updatedContent, mockResponseJson);

        CommentResponse response = commentSteps.updateComment(getMockBaseUrl(), generatedId, updatedContent);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getContent()).isEqualTo(updatedContent);
    }

    @Test
    @Story("Удаление комментария")
    @DisplayName("Успешное удаление комментария")
    void shouldDeleteCommentSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubDeleteSuccess(wireMockServer, "/comments/" + generatedId);

        commentSteps.deleteComment(getMockBaseUrl(), generatedId);
    }
}