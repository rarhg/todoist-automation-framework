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

    public static final RequestSpecification PRODUCTION_REQUEST_SPEC = build(
            baseSpec(ConfigProvider.CONFIG.apiUrl())
                    .addHeader("Authorization", bearer(ConfigProvider.CONFIG.apiToken()))
                    .addFilter(new RetryOnServerErrorFilter(3, 1000)));

    private Specs() {
    }

    public static RequestSpecification getRequestSpec(String baseUrl) {
        return build(baseSpec(baseUrl)
                .addHeader("Authorization", bearer(ConfigProvider.CONFIG.mockTestToken())));
    }

    public static RequestSpecification getRequestSpecWithoutToken(String baseUrl) {
        return build(baseSpec(baseUrl));
    }

    public static RequestSpecification getRequestSpecWithInvalidToken(String baseUrl) {
        return build(baseSpec(baseUrl)
                .addHeader("Authorization", bearer(ConfigProvider.CONFIG.mockInvalidToken())));
    }

    public static ResponseSpecification getResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .log(LogDetail.ALL)
                .build();
    }

    private static RequestSpecBuilder baseSpec(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setBasePath(ConfigProvider.CONFIG.apiVersion())
                .setContentType(ContentType.JSON);
    }

    private static RequestSpecification build(RequestSpecBuilder builder) {
        return builder
                .addFilter(new AllureRestAssured()
                        .setRequestTemplate(REQUEST_TEMPLATE)
                        .setResponseTemplate(RESPONSE_TEMPLATE))
                .log(LogDetail.ALL)
                .build();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
