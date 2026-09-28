package todoist.api.specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import todoist.config.ConfigProvider;

public final class Specs {

    private static final String REQUEST_TEMPLATE = "todoist-http-request.ftl";
    private static final String RESPONSE_TEMPLATE = "todoist-http-response.ftl";

    private static AllureRestAssured allureFilter() {
        return new AllureRestAssured()
                .setRequestTemplate(REQUEST_TEMPLATE)
                .setResponseTemplate(RESPONSE_TEMPLATE);
    }

    public static final RequestSpecification PRODUCTION_REQUEST_SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigProvider.CONFIG.apiUrl())
            .setBasePath(ConfigProvider.CONFIG.apiVersion())
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + ConfigProvider.CONFIG.apiToken())
            .addFilter(new RetryOnServerErrorFilter(3, 1000))
            .addFilter(allureFilter())
            .log(LogDetail.ALL)
            .build();

    private Specs() {
    }

    public static RequestSpecification getRequestSpec(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath(ConfigProvider.CONFIG.apiVersion())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ConfigProvider.CONFIG.mockTestToken())
                .addFilter(allureFilter())
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getRequestSpecWithoutToken(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath(ConfigProvider.CONFIG.apiVersion())
                .setContentType(ContentType.JSON)
                .addFilter(allureFilter())
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getRequestSpecWithInvalidToken(String baseUrl) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setBasePath(ConfigProvider.CONFIG.apiVersion())
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + ConfigProvider.CONFIG.mockInvalidToken())
                .addFilter(allureFilter())
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification getResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }
}