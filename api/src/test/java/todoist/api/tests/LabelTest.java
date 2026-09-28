package todoist.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import todoist.api.models.LabelRequest;
import todoist.api.models.LabelResponse;
import todoist.api.steps.LabelSteps;
import todoist.api.stubs.WireMockStubs;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление метками")
@Feature("API: CRUD-операции с метками")
@Tag("api")
@DisplayName("API: Операции с метками Todoist")
@Execution(ExecutionMode.CONCURRENT)
public class LabelTest extends BaseApiTest {

    private final LabelSteps labelSteps = new LabelSteps();

    @Test
    @Story("Создание метки")
    @DisplayName("Успешное создание персональной метки")
    void shouldCreateLabelSuccessfully() {
        Faker faker = new Faker();
        String labelName = faker.lorem().word();
        String generatedId = faker.internet().uuid();

        LabelRequest request = LabelRequest.builder()
                .name(labelName)
                .color("green")
                .isFavorite(false)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"green\",\"is_favorite\":false,\"order\":0}",
                generatedId, labelName
        );

        WireMockStubs.stubPostSuccess(wireMockServer, "/labels", "$.name", labelName, mockResponseJson);

        LabelResponse response = labelSteps.createLabel(getMockBaseUrl(), request);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(labelName);
    }

    @Test
    @Story("Удаление метки")
    @DisplayName("Успешное удаление метки")
    void shouldDeleteLabelSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();

        WireMockStubs.stubDeleteSuccess(wireMockServer, String.format("/labels/%s", generatedId));

        labelSteps.deleteLabel(getMockBaseUrl(), generatedId);
    }

    @Test
    @Story("Получение метки")
    @DisplayName("Успешное получение метки по ID")
    void shouldGetLabelByIdSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String labelName = faker.lorem().word();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"green\",\"is_favorite\":false}",
                generatedId, labelName
        );

        WireMockStubs.stubGetSuccess(wireMockServer, "/labels/" + generatedId, mockResponseJson);

        LabelResponse response = labelSteps.getLabelById(getMockBaseUrl(), generatedId);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(labelName);
    }

    @Test
    @Story("Получение списка меток")
    @DisplayName("Успешное получение списка всех меток")
    void shouldGetAllLabelsSuccessfully() {
        Faker faker = new Faker();
        String id1 = faker.internet().uuid();
        String id2 = faker.internet().uuid();

        String mockResponseJson = String.format(
                "[{\"id\":\"%s\",\"name\":\"work\",\"color\":\"red\",\"is_favorite\":false},"
                        + "{\"id\":\"%s\",\"name\":\"home\",\"color\":\"blue\",\"is_favorite\":true}]",
                id1, id2
        );

        WireMockStubs.stubGetSuccess(wireMockServer, "/labels", mockResponseJson);

        LabelResponse[] response = labelSteps.getAllLabels(getMockBaseUrl());

        assertThat(response).hasSize(2);
        assertThat(response[0].getId()).isEqualTo(id1);
    }

    @Test
    @Story("Обновление метки")
    @DisplayName("Успешное обновление метки")
    void shouldUpdateLabelSuccessfully() {
        Faker faker = new Faker();
        String generatedId = faker.internet().uuid();
        String updatedName = faker.lorem().word();

        LabelRequest updateRequest = LabelRequest.builder()
                .name(updatedName)
                .build();

        String mockResponseJson = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"color\":\"green\",\"is_favorite\":false}",
                generatedId, updatedName
        );

        WireMockStubs.stubUpdateSuccess(wireMockServer, "/labels/" + generatedId, "$.name", updatedName, mockResponseJson);

        LabelResponse response = labelSteps.updateLabel(getMockBaseUrl(), generatedId, updateRequest);

        assertThat(response.getId()).isEqualTo(generatedId);
        assertThat(response.getName()).isEqualTo(updatedName);
    }
}