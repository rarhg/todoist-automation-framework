package todoist.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import todoist.api.models.ProjectRequest;
import todoist.api.models.ProjectResponse;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.*;

public class ProjectSteps {

    @Step("Создать проект через API (по умолчанию)")
    public ProjectResponse createProject(String baseUrl, ProjectRequest request) {
        return createProject(request, getRequestSpec(baseUrl));
    }

    @Step("Создать проект через API с кастомной спецификацией")
    public ProjectResponse createProject(ProjectRequest request, RequestSpecification spec) {
        return given()
                .spec(spec)
                .body(request)
                .when()
                .post("/projects")
                .then()
                .spec(getResponseSpec(201))
                .extract().as(ProjectResponse.class);
    }

    @Step("Попытка создать проект без токена авторизации")
    public Response createProjectWithoutToken(String baseUrl, ProjectRequest request) {
        return given()
                .spec(getRequestSpecWithoutToken(baseUrl))
                .body(request)
                .when()
                .post("/projects")
                .then()
                .extract().response();
    }

    @Step("Попытка создать проект с невалидным токеном авторизации")
    public Response createProjectWithInvalidToken(String baseUrl, ProjectRequest request) {
        return given()
                .spec(getRequestSpecWithInvalidToken(baseUrl))
                .body(request)
                .when()
                .post("/projects")
                .then()
                .extract().response();
    }

    @Step("Получить проект по ID: {id}")
    public ProjectResponse getProjectById(String baseUrl, String id) {
        return getProjectById(id, getRequestSpec(baseUrl));
    }

    @Step("Получить проект по ID: {id} с кастомной спецификацией")
    public ProjectResponse getProjectById(String id, RequestSpecification spec) {
        return given()
                .spec(spec)
                .pathParam("id", id)
                .when()
                .get("/projects/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(ProjectResponse.class);
    }

    @Step("Получить список всех проектов через API")
    public ProjectResponse[] getAllProjects(RequestSpecification spec) {
        return given()
                .spec(spec)
                .when()
                .get("/projects")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .jsonPath()
                .getObject("results", ProjectResponse[].class);
    }

    @Step("Удалить проект по ID: {id}")
    public void deleteProject(String baseUrl, String id) {
        deleteProject(id, getRequestSpec(baseUrl));
    }

    @Step("Удалить проект по ID: {id} с кастомной спецификацией")
    public void deleteProject(String id, RequestSpecification spec) {
        given()
                .spec(spec)
                .pathParam("id", id)
                .when()
                .delete("/projects/{id}")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Обновить проект по ID: {id}")
    public ProjectResponse updateProject(String baseUrl, String id, ProjectRequest request) {
        return updateProject(id, request, getRequestSpec(baseUrl));
    }

    @Step("Обновить проект по ID: {id} с кастомной спецификацией")
    public ProjectResponse updateProject(String id, ProjectRequest request, RequestSpecification spec) {
        return given()
                .spec(spec)
                .pathParam("id", id)
                .body(request)
                .when()
                .post("/projects/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(ProjectResponse.class);
    }

    @Step("Попытка создать проект с невалидными данными")
    public Response createProjectInvalid(String baseUrl, ProjectRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/projects")
                .then()
                .spec(getResponseSpec(400))
                .extract().response();
    }
}
