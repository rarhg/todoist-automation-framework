package todoist.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import todoist.api.models.TaskRequest;
import todoist.api.models.TaskResponse;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.*;

public class TaskApiSteps {

    @Step("Получить список всех активных задач через API (WireMock)")
    public TaskResponse[] getAllActiveTasks(String baseUrl) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .when()
                .get("/tasks")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .jsonPath()
                .getObject("results", TaskResponse[].class);
    }

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

    @Step("Создать задачу через API (WireMock)")
    public TaskResponse createTask(String baseUrl, TaskRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/tasks")
                .then()
                .spec(getResponseSpec(201))
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
