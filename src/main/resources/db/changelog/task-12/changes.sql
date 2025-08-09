ALTER TABLE users RENAME COLUMN login TO username;

ALTER TABLE users
DROP CONSTRAINT IF EXISTS users_role_check,
    ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER'));
