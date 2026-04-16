CREATE TABLE IF NOT EXISTS app_user (
    id            UUID PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created       TIMESTAMP    NOT NULL
);

ALTER TABLE task_list
    ADD COLUMN owner_id UUID REFERENCES app_user (id) ON DELETE CASCADE;

DO $$
DECLARE
    seed_id UUID;
BEGIN
    IF EXISTS (SELECT 1 FROM task_list WHERE owner_id IS NULL) THEN
        seed_id := gen_random_uuid();
        INSERT INTO app_user (id, username, password_hash, created)
        VALUES (seed_id,
                'legacy',
                '{bcrypt}$2a$10$placeholderplaceholderplaceholderplaceholderpla',
                NOW());
        UPDATE task_list SET owner_id = seed_id WHERE owner_id IS NULL;
    END IF;
END $$;

ALTER TABLE task_list
    ALTER COLUMN owner_id SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_task_list_owner_id ON task_list (owner_id);
