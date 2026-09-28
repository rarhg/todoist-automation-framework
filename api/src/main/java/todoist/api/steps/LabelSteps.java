package todoist.api.steps;

import io.qameta.allure.Step;
import todoist.api.models.LabelRequest;
import todoist.api.models.LabelResponse;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.getRequestSpec;
import static todoist.api.specs.Specs.getResponseSpec;

public class LabelSteps {

    @Step("Создать метку через API")
    public LabelResponse createLabel(String baseUrl, LabelRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .body(request)
                .when()
                .post("/labels")
                .then()
                .spec(getResponseSpec(201))
                .extract().as(LabelResponse.class);
    }

    @Step("Удалить метку по ID: {id}")
    public void deleteLabel(String baseUrl, String id) {
        given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .delete("/labels/{id}")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Получить метку по ID: {id}")
    public LabelResponse getLabelById(String baseUrl, String id) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .when()
                .get("/labels/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(LabelResponse.class);
    }

    @Step("Получить список всех меток через API")
    public LabelResponse[] getAllLabels(String baseUrl) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .when()
                .get("/labels")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(LabelResponse[].class);
    }

    @Step("Обновить метку по ID: {id}")
    public LabelResponse updateLabel(String baseUrl, String id, LabelRequest request) {
        return given()
                .spec(getRequestSpec(baseUrl))
                .pathParam("id", id)
                .body(request)
                .when()
                .post("/labels/{id}")
                .then()
                .spec(getResponseSpec(200))
                .extract().as(LabelResponse.class);
    }
}
