ALTER TABLE users
    RENAME COLUMN username TO lgin;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_role_check,
    ADD CONSTRAINT users_role_check CHECK (role IN ('admin', 'user'));
