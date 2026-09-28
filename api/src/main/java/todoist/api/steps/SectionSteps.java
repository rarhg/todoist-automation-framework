package todoist.api.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import todoist.api.models.SectionRequest;
import todoist.api.models.SectionResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static todoist.api.specs.Specs.getRequestSpec;
import static todoist.api.specs.Specs.getResponseSpec;

public class SectionSteps {

    @Step("Создать секцию через API")
    public SectionResponse createSection(String baseUrl, SectionRequest request) {
        return createSection(request, getRequestSpec(baseUrl));
    }

    @Step("Создать секцию через API с кастомной спецификацией")
    public SectionResponse createSection(SectionRequest request, RequestSpecification spec) {
        return given()
                .spec(spec)
                .body(request)
                .when()
                .post("/sections")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .extract().as(SectionResponse.class);
    }

    @Step("Попытка создать секцию с некорректными данными")
    public Response createSectionInvalid(String baseUrl, SectionRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/sections")
                .then()
                .spec(getResponseSpec(400))
                .extract().response();
    }

    @Step("Получить секцию по ID: {id}")
    public Response getSectionById(String baseUrl, String id, int expectedStatus) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .get("/sections/{id}")
                .then()
                .spec(getResponseSpec(expectedStatus))
                .extract().response();
    }

    @Step("Обновить секцию по ID: {id}")
    public SectionResponse updateSection(String baseUrl, String id, SectionRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .body(request)
                .when()
                .post("/sections/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(SectionResponse.class);
    }

    @Step("Удалить секцию по ID: {id}")
    public void deleteSection(String baseUrl, String id) {
        deleteSection(id, getRequestSpec(baseUrl));
    }

    @Step("Удалить секцию по ID: {id} с кастомной спецификацией")
    public void deleteSection(String id, RequestSpecification spec) {
        given()
                .spec(spec)
                .pathParam("id", id)
                .when()
                .delete("/sections/{id}")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Получить список разделов проекта: {projectId}")
    public List<SectionResponse> getSectionsByProject(String projectId, RequestSpecification spec) {
        return given()
                .spec(spec)
                .queryParam("project_id", projectId)
                .when()
                .get("/sections")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("results", SectionResponse.class);
    }
}