package todoist.api.steps;

import io.qameta.allure.Step;
import todoist.api.models.TaskResponse;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.PRODUCTION_REQUEST_SPEC;
import static todoist.api.specs.Specs.getResponseSpec;

public class TaskProductionSteps {

    @Step("Создать новую задачу '{taskName}' через PRODUCTION API")
    public TaskResponse createTask(String taskName) {
        return given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .body(Map.of("content", taskName))
                .when()
                .post("/tasks")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .as(TaskResponse.class);
    }

    @Step("Получить список всех активных задач через PRODUCTION API")
    public TaskResponse[] getAllActiveTasks() {
        return given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .when()
                .get("/tasks")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .jsonPath()
                .getObject("results", TaskResponse[].class);
    }

    @Step("Закрыть (выполнить) задачу по ID: {id} через PRODUCTION API")
    public void closeTask(String id) {
        given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .pathParam("id", id)
                .when()
                .post("/tasks/{id}/close")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Удалить задачу по ID: {id} через PRODUCTION API")
    public void deleteTask(String id) {
        given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .pathParam("id", id)
                .when()
                .delete("/tasks/{id}")
                .then()
                .spec(getResponseSpec(204));
    }
}
