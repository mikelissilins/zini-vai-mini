ALTER TABLE questions
    ADD COLUMN time_limit_seconds INTEGER NOT NULL DEFAULT 40 CHECK (time_limit_seconds BETWEEN 5 AND 300);

ALTER TABLE session_questions
    ADD COLUMN time_limit_seconds INTEGER NOT NULL DEFAULT 40 CHECK (time_limit_seconds BETWEEN 5 AND 300);
