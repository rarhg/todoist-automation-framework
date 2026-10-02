package todoist.api.steps;

import io.qameta.allure.Step;
import todoist.api.models.CommentRequest;
import todoist.api.models.CommentResponse;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.getRequestSpec;
import static todoist.api.specs.Specs.getResponseSpec;

public class CommentSteps {

    @Step("Создать комментарий через API")
    public CommentResponse createComment(String baseUrl, CommentRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/comments")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(CommentResponse.class);
    }

    @Step("Получить комментарий по ID: {id}")
    public CommentResponse getCommentById(String baseUrl, String id) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .get("/comments/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(CommentResponse.class);
    }

    @Step("Получить список комментариев по задаче: {taskId}")
    public CommentResponse[] getAllComments(String baseUrl, String taskId) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .queryParam("task_id", taskId)
                .when()
                .get("/comments")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .jsonPath()
                .getObject("results", CommentResponse[].class);
    }

    @Step("Обновить комментарий по ID: {id}")
    public CommentResponse updateComment(String baseUrl, String id, String newContent) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .body(Map.of("content", newContent))
                .when()
                .post("/comments/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(CommentResponse.class);
    }

    @Step("Удалить комментарий по ID: {id}")
    public void deleteComment(String baseUrl, String id) {
        given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .delete("/comments/{id}")
                .then()
                .spec(getResponseSpec(204));
    }
}