package todoist.api.steps;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import todoist.api.models.TaskResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static io.restassured.RestAssured.given;
import static todoist.api.specs.Specs.PRODUCTION_REQUEST_SPEC;
import static todoist.api.specs.Specs.getResponseSpec;

public class TaskProductionSteps {

    private static final int PAGE_LIMIT = 200;

    @Step("Создать новую задачу '{taskName}' через PRODUCTION API")
    public TaskResponse createTask(String taskName) {
        return given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .body(Map.of("content", taskName))
                .when()
                .post("/tasks")
                .then()
                .spec(getResponseSpec(200))
                .extract()
                .as(TaskResponse.class);
    }

    @Step("Получить список всех активных задач через PRODUCTION API (со всеми страницами)")
    public TaskResponse[] getAllActiveTasks() {
        List<TaskResponse> all = new ArrayList<>();
        String cursor = null;
        do {
            var request = given()
                    .spec(PRODUCTION_REQUEST_SPEC)
                    .queryParam("limit", PAGE_LIMIT);
            if (cursor != null) {
                request = request.queryParam("cursor", cursor);
            }
            JsonPath body = request
                    .when()
                    .get("/tasks")
                    .then()
                    .spec(getResponseSpec(200))
                    .extract()
                    .jsonPath();
            List<TaskResponse> page = body.getList("results", TaskResponse.class);
            if (page != null) {
                all.addAll(page);
            }
            cursor = body.getString("next_cursor");
        } while (cursor != null);
        return all.toArray(new TaskResponse[0]);
    }

    @Step("Найти активные задачи, название которых начинается с '{prefix}'")
    public List<TaskResponse> findActiveTasksByContentPrefix(String prefix) {
        return findActiveTasks(content -> content.startsWith(prefix));
    }

    @Step("Дождаться появления активной задачи '{content}' на сервере")
    public TaskResponse awaitActiveTaskByContent(String content, Duration timeout) {
        return awaitActiveTask(content, c -> content.equals(c.trim()), timeout);
    }

    @Step("Дождаться появления активной задачи с названием, начинающимся с '{prefix}', на сервере")
    public TaskResponse awaitActiveTaskByContentPrefix(String prefix, Duration timeout) {
        return awaitActiveTask(prefix, c -> c.startsWith(prefix), timeout);
    }

    @Step("Закрыть (выполнить) задачу по ID: {id} через PRODUCTION API")
    public void closeTask(String id) {
        given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .pathParam("id", id)
                .when()
                .post("/tasks/{id}/close")
                .then()
                .spec(getResponseSpec(204));
    }

    @Step("Удалить задачу по ID: {id} через PRODUCTION API")
    public void deleteTask(String id) {
        given()
                .spec(PRODUCTION_REQUEST_SPEC)
                .pathParam("id", id)
                .when()
                .delete("/tasks/{id}")
                .then()
                .spec(getResponseSpec(204));
    }

    private List<TaskResponse> findActiveTasks(Predicate<String> contentMatcher) {
        return Arrays.stream(getAllActiveTasks())
                .filter(t -> t.getContent() != null && contentMatcher.test(t.getContent()))
                .toList();
    }

    private TaskResponse awaitActiveTask(String description, Predicate<String> contentMatcher, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (true) {
            List<TaskResponse> found = findActiveTasks(contentMatcher);
            if (!found.isEmpty()) {
                return found.get(0);
            }
            if (System.nanoTime() >= deadline) {
                throw new AssertionError("Задача '" + description + "' не появилась на сервере за "
                        + timeout.toSeconds() + " с");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Ожидание задачи '" + description + "' прервано", e);
            }
        }
    }
}
