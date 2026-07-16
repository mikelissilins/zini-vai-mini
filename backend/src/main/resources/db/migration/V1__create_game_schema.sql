CREATE TABLE media_assets (
    id UUID PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(80) NOT NULL,
    byte_size BIGINT NOT NULL CHECK (byte_size BETWEEN 1 AND 5242880),
    data BYTEA NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE games (
    id UUID PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(600),
    locale VARCHAR(5) NOT NULL,
    template BOOLEAN NOT NULL DEFAULT FALSE,
    template_key VARCHAR(80) UNIQUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    name VARCHAR(80) NOT NULL,
    color VARCHAR(7) NOT NULL,
    position INTEGER NOT NULL,
    UNIQUE (game_id, position)
);

CREATE TABLE questions (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    media_asset_id UUID REFERENCES media_assets(id) ON DELETE SET NULL,
    points INTEGER NOT NULL CHECK (points IN (10, 20, 30, 40, 50)),
    question_type VARCHAR(24) NOT NULL,
    prompt VARCHAR(1200) NOT NULL DEFAULT '',
    answer VARCHAR(1200) NOT NULL DEFAULT '',
    explanation VARCHAR(1600),
    UNIQUE (category_id, points)
);

CREATE TABLE question_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    text VARCHAR(600) NOT NULL,
    position INTEGER NOT NULL,
    correct BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (question_id, position)
);

CREATE INDEX idx_categories_game ON categories(game_id);
CREATE INDEX idx_questions_category ON questions(category_id);
CREATE INDEX idx_options_question ON question_options(question_id);
