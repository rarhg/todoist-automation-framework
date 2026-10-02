package todoist.db;

import io.qameta.allure.Step;
import todoist.api.models.TaskResponse;
import todoist.api.steps.TaskProductionSteps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TaskSyncService {

    private final TaskProductionSteps apiSteps;
    private final SyncedTaskDao dao;

    public TaskSyncService(TaskProductionSteps apiSteps, SyncedTaskDao dao) {
        this.apiSteps = apiSteps;
        this.dao = dao;
    }

    @Step("Синхронизировать активные задачи из Todoist в локальную БД")
    public Set<String> syncActiveTasks() {
        Set<String> activeIds = new HashSet<>();

        for (TaskResponse task : apiSteps.getAllActiveTasks()) {
            dao.upsert(task.getId(), task.getContent(), "ACTIVE");
            activeIds.add(task.getId());
        }
        return activeIds;
    }

    @Step("Пометить как COMPLETED задачи, пропавшие из списка активных")
    public void reconcileCompleted(Collection<String> previouslyActiveIds) {
        Set<String> stillActive = syncActiveTasks();

        for (String taskId : previouslyActiveIds) {
            if (!stillActive.contains(taskId)) {
                dao.findById(taskId).ifPresent(record ->
                        dao.upsert(record.taskId(), record.content(), "COMPLETED"));
            }
        }
    }
}
