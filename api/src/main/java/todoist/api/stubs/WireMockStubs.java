package todoist.api.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import todoist.config.ConfigProvider;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class WireMockStubs {

    private static final String API_VERSION = ConfigProvider.CONFIG.apiVersion();

    private WireMockStubs() {
    }

    public static void stubPostSuccess(WireMockServer wireMock, String endpoint, String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        wireMock.stubFor(post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)))
                .atPriority(1)
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
    }

    public static void stubUpdateSuccess(WireMockServer wireMock, String endpointWithId, String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        wireMock.stubFor(post(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
    }

    public static void stubGetSuccess(WireMockServer wireMock, String endpointWithId, String responseBodyJson) {
        wireMock.stubFor(get(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
    }

    public static void stubDeleteSuccess(WireMockServer wireMock, String endpointWithId) {
        wireMock.stubFor(delete(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    public static void stubPostNoContent(WireMockServer wireMock, String endpoint) {
        wireMock.stubFor(post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    public static void stubUnauthorizedMissingToken(WireMockServer wireMock, String method, String endpoint) {
        var mappingBuilder = switch (method.toUpperCase()) {
            case "POST" -> post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            case "GET" -> get(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            case "DELETE" -> delete(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            default -> any(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
        };

        wireMock.stubFor(mappingBuilder
                .withHeader("Authorization", absent())
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"error\": \"Unauthorized\"}")));
    }

    public static void stubUnauthorizedInvalidToken(WireMockServer wireMock, String method, String endpoint, String invalidToken) {
        var mappingBuilder = switch (method.toUpperCase()) {
            case "POST" -> post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            case "GET" -> get(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            case "DELETE" -> delete(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
            default -> any(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
        };

        wireMock.stubFor(mappingBuilder
                .withHeader("Authorization", equalTo("Bearer " + invalidToken))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("{\"error\": \"Unauthorized\"}")));
    }

    public static void stubBadRequest(WireMockServer wireMock, String method, String endpoint,
                                      String expectedJsonPathField, String expectedValue, String responseBodyJson) {
        var mappingBuilder = switch (method.toUpperCase()) {
            case "POST" -> post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)))
                    .withRequestBody(matchingJsonPath(expectedJsonPathField, equalTo(expectedValue)));
            default -> any(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)));
        };

        wireMock.stubFor(mappingBuilder
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
    }

    public static void stubNotFound(WireMockServer wireMock, String method, String endpointWithId) {
        var mappingBuilder = switch (method.toUpperCase()) {
            case "GET" -> get(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)));
            case "DELETE" -> delete(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)));
            default -> any(urlEqualTo(String.format("%s%s", API_VERSION, endpointWithId)));
        };

        wireMock.stubFor(mappingBuilder
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"error\": \"Resource not found\"}")));
    }

    public static void stubPostSuccess(WireMockServer wireMock, String endpoint, Map<String, String> expectedJsonPathFields, String responseBodyJson) {
        var mappingBuilder = post(urlEqualTo(String.format("%s%s", API_VERSION, endpoint)))
                .atPriority(1)
                .withHeader("Content-Type", containing("application/json"));

        for (var entry : expectedJsonPathFields.entrySet()) {
            mappingBuilder = mappingBuilder.withRequestBody(matchingJsonPath(entry.getKey(), equalTo(entry.getValue())));
        }

        wireMock.stubFor(mappingBuilder
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
    }
}
