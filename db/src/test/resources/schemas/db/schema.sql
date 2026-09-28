CREATE TABLE synced_tasks (
    task_id     VARCHAR(64) PRIMARY KEY,
    content     TEXT NOT NULL,
    status      VARCHAR(20) NOT NULL,
    synced_at   TIMESTAMP NOT NULL DEFAULT now()
);