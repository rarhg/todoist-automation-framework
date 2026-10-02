package todoist.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import todoist.api.models.TaskRequest;
import todoist.api.models.TaskResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static todoist.api.specs.Specs.*;

public class TaskApiSteps {

    private static final String TASK_SCHEMA = "schemas/task-response-schema.json";

    @Step("Получить задачу по ID: {id} через API (WireMock)")
    public TaskResponse getTaskById(String baseUrl, String id) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .get("/tasks/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(TaskResponse.class);
    }

    @Step("Попытка получить задачу по невалидному ID: {id} через API (WireMock)")
    public Response getTaskByInvalidId(String baseUrl, String id, int expectedStatus) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .get("/tasks/{id}")
                .then()
                .spec(getResponseSpec(expectedStatus))
                .extract().response();
    }

    @Step("Закрыть (выполнить) задачу по ID: {id} через API (WireMock)")
    public void closeTask(String baseUrl, String id) {
        given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .post("/tasks/{id}/close")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Удалить задачу по ID: {id} через API (WireMock)")
    public void deleteTask(String baseUrl, String id) {
        given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .delete("/tasks/{id}")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Создать задачу через API (WireMock) и проверить контракт ответа по JSON-схеме")
    public TaskResponse createTaskWithContractCheck(String baseUrl, TaskRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/tasks")
                .then()
                .spec(getResponseSpec(200))
                .body(matchesJsonSchemaInClasspath(TASK_SCHEMA))
                .extract().as(TaskResponse.class);
    }

    @Step("Попытка создать задачу с невалидными данными через API (WireMock)")
    public Response createTaskInvalid(String baseUrl, TaskRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/tasks")
                .then()
                .spec(getResponseSpec(400))
                .extract().response();
    }

    @Step("Обновить задачу по ID: {id} через API (WireMock)")
    public TaskResponse updateTask(String baseUrl, String id, TaskRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .body(request)
                .when()
                .post("/tasks/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(TaskResponse.class);
    }
}