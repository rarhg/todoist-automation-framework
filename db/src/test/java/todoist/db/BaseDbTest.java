package todoist.db;

import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Tag("db")
@Testcontainers
public abstract class BaseDbTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("todoist_sync")
                    .withInitScript("schemas/db/schema.sql");

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    protected SyncedTaskDao dao() {
        return new SyncedTaskDao(this::getConnection);
    }
}
