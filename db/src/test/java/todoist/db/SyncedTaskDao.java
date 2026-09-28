package todoist.db;

import io.qameta.allure.Step;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SyncedTaskDao {

    @FunctionalInterface
    public interface ConnectionSupplier {
        Connection get() throws SQLException;
    }

    private final ConnectionSupplier connectionSupplier;

    public SyncedTaskDao(ConnectionSupplier connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Step("Записать/обновить синхронизированную задачу: {taskId} -> {status}")
    public void upsert(String taskId, String content, String status) {
        String sql = """
                INSERT INTO synced_tasks (task_id, content, status, synced_at)
                VALUES (?, ?, ?, now())
                ON CONFLICT (task_id)
                DO UPDATE SET content = EXCLUDED.content,
                              status = EXCLUDED.status,
                              synced_at = now()
                """;

        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, taskId);
            statement.setString(2, content);
            statement.setString(3, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось записать задачу " + taskId + " в synced_tasks", e);
        }
    }

    @Step("Найти синхронизированную задачу по ID: {taskId}")
    public Optional<SyncedTaskRecord> findById(String taskId) {
        String sql = "SELECT task_id, content, status FROM synced_tasks WHERE task_id = ?";

        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, taskId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new SyncedTaskRecord(
                        rs.getString("task_id"),
                        rs.getString("content"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось прочитать задачу " + taskId + " из synced_tasks", e);
        }
    }

    @Step("Получить все синхронизированные задачи")
    public List<SyncedTaskRecord> findAll() {
        String sql = "SELECT task_id, content, status FROM synced_tasks";
        List<SyncedTaskRecord> result = new ArrayList<>();

        try (Connection connection = connectionSupplier.get();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new SyncedTaskRecord(
                        rs.getString("task_id"),
                        rs.getString("content"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось прочитать synced_tasks", e);
        }
        return result;
    }

    public record SyncedTaskRecord(String taskId, String content, String status) {
    }
}
