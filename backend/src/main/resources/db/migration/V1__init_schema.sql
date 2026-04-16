CREATE TABLE IF NOT EXISTS task_list (
    id          UUID PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    created     TIMESTAMP    NOT NULL,
    updated     TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS task (
    id           UUID PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(2000),
    due_date     TIMESTAMP,
    status       VARCHAR(32)  NOT NULL,
    priority     VARCHAR(32)  NOT NULL,
    task_list_id UUID         REFERENCES task_list (id) ON DELETE CASCADE,
    created      TIMESTAMP    NOT NULL,
    updated      TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_task_task_list_id ON task (task_list_id);