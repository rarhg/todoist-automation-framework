package todoist.db.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskProductionSteps;
import todoist.db.BaseDbTest;
import todoist.db.SyncedTaskDao;
import todoist.db.TaskSyncService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Управление задачами")
@Feature("DB: Синхронизация задач с локальной БД")
@Tag("db")
@DisplayName("DB: Синхронизация состояния задач Todoist в локальную БД")
public class TaskSyncTest extends BaseDbTest {

    private final TaskProductionSteps apiSteps = new TaskProductionSteps();
    private String createdTaskId;

    @Test
    @Story("Отражение статуса выполнения в БД")
    @DisplayName("Закрытие задачи в Todoist отражается статусом COMPLETED в локальной БД")
    void shouldReflectTaskCompletionInDatabase() {
        Faker faker = new Faker();
        SyncedTaskDao dao = dao();
        TaskSyncService syncService = new TaskSyncService(apiSteps, dao);

        String taskName = faker.book().title() + " " + System.currentTimeMillis();
        TaskResponse created = apiSteps.createTask(taskName);
        createdTaskId = created.getId();

        Set<String> activeIds = syncService.syncActiveTasks();
        assertThat(activeIds).contains(createdTaskId);

        var activeRecord = dao.findById(createdTaskId);
        assertThat(activeRecord).isPresent();
        assertThat(activeRecord.get().status()).isEqualTo("ACTIVE");

        apiSteps.closeTask(createdTaskId);

        syncService.reconcileCompleted(activeIds);

        var completedRecord = dao.findById(createdTaskId);
        assertThat(completedRecord).isPresent();
        assertThat(completedRecord.get().status()).isEqualTo("COMPLETED");
    }

    @AfterEach
    void cleanUp() {
        if (createdTaskId != null) {
            try {
                apiSteps.deleteTask(createdTaskId);
            } catch (Exception ignored) {
            }
        }
    }
}