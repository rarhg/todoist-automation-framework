package todoist.api.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import todoist.config.ConfigProvider;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class WireMockStubs {

    private static final String API_VERSION = ConfigProvider.CONFIG.apiVersion();

    private WireMockStubs() {
    }

    public static void stubPostSuccess(WireMockServer wireMock, String endpoint, String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        wireMock.stubFor(post(urlEqualTo(API_VERSION + endpoint))
                .atPriority(1)
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)))
                .willReturn(jsonResponse(200, responseBodyJson)));
    }

    public static void stubUpdateSuccess(WireMockServer wireMock, String endpointWithId, String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        wireMock.stubFor(post(urlEqualTo(API_VERSION + endpointWithId))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)))
                .willReturn(jsonResponse(200, responseBodyJson)));
    }

    public static void stubGetSuccess(WireMockServer wireMock, String endpointWithId, String responseBodyJson) {
        wireMock.stubFor(get(urlEqualTo(API_VERSION + endpointWithId))
                .willReturn(jsonResponse(200, responseBodyJson)));
    }

    public static void stubDeleteSuccess(WireMockServer wireMock, String endpointWithId) {
        wireMock.stubFor(delete(urlEqualTo(API_VERSION + endpointWithId))
                .willReturn(aResponse().withStatus(204)));
    }

    public static void stubPostNoContent(WireMockServer wireMock, String endpoint) {
        wireMock.stubFor(post(urlEqualTo(API_VERSION + endpoint))
                .willReturn(aResponse().withStatus(204)));
    }

    public static void stubUnauthorizedMissingToken(WireMockServer wireMock, String method, String endpoint) {
        stubUnauthorized(wireMock, method, endpoint, absent());
    }

    public static void stubUnauthorizedInvalidToken(WireMockServer wireMock, String method, String endpoint, String invalidToken) {
        stubUnauthorized(wireMock, method, endpoint, equalTo("Bearer " + invalidToken));
    }

    public static void stubBadRequest(WireMockServer wireMock, String method, String endpoint,
                                      String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        MappingBuilder mapping = mapping(method, endpoint);
        if ("POST".equalsIgnoreCase(method)) {
            mapping = mapping.withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)));
        }
        wireMock.stubFor(mapping.willReturn(jsonResponse(400, responseBodyJson)));
    }

    public static void stubNotFound(WireMockServer wireMock, String method, String endpointWithId) {
        wireMock.stubFor(mapping(method, endpointWithId)
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\": \"Resource not found\"}")));
    }

    private static void stubUnauthorized(WireMockServer wireMock, String method, String endpoint,
                                         StringValuePattern authorizationHeader) {
        wireMock.stubFor(mapping(method, endpoint)
                .withHeader("Authorization", authorizationHeader)
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"error\": \"Unauthorized\"}")));
    }

    private static MappingBuilder mapping(String method, String endpoint) {
        var url = urlEqualTo(API_VERSION + endpoint);
        return switch (method.toUpperCase()) {
            case "POST" -> post(url);
            case "GET" -> get(url);
            case "DELETE" -> delete(url);
            default -> any(url);
        };
    }

    private static ResponseDefinitionBuilder jsonResponse(int status, String body) {
        return aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(body);
    }
}
