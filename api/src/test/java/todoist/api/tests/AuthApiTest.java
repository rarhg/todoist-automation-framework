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
import todoist.api.models.ProjectRequest;
import todoist.api.steps.ProjectSteps;
import todoist.config.ConfigProvider;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Авторизация и аккаунт")
@Feature("API: Авторизация запросов")
@Tag("api")
@Tag("live")
@DisplayName("API: Проверка авторизации на реальном Todoist API")
@Execution(ExecutionMode.CONCURRENT)
public class AuthApiTest {

    private final ProjectSteps projectSteps = new ProjectSteps();
    private final String realApiUrl = ConfigProvider.CONFIG.apiUrl();

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание проекта без авторизационного токена (401/403)")
    void shouldRejectRequestWithoutToken() {
        ProjectRequest request = ProjectRequest.builder()
                .name(new Faker().commerce().department())
                .build();

        var response = projectSteps.createProjectWithoutToken(realApiUrl, request);

        assertThat(response.getStatusCode()).isIn(401, 403);
    }

    @Test
    @Story("Негативные сценарии")
    @DisplayName("Негативный кейс: Создание проекта с невалидным токеном (401/403)")
    void shouldRejectRequestWithInvalidToken() {
        ProjectRequest request = ProjectRequest.builder()
                .name(new Faker().commerce().department())
                .build();

        var response = projectSteps.createProjectWithInvalidToken(realApiUrl, request);

        assertThat(response.getStatusCode()).isIn(401, 403);
    }
}