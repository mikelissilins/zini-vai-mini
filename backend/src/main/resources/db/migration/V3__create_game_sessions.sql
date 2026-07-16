CREATE TABLE game_sessions (
    id UUID PRIMARY KEY,
    game_id UUID NOT NULL REFERENCES games(id),
    public_token VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(160) NOT NULL,
    locale VARCHAR(5) NOT NULL,
    status VARCHAR(24) NOT NULL,
    active_team_index INTEGER NOT NULL DEFAULT 0,
    selected_question_id UUID,
    answer_revealed BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE session_teams (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES game_sessions(id) ON DELETE CASCADE,
    name VARCHAR(80) NOT NULL,
    color VARCHAR(7) NOT NULL,
    position INTEGER NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    UNIQUE (session_id, position)
);

CREATE TABLE session_questions (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES game_sessions(id) ON DELETE CASCADE,
    source_question_id UUID,
    media_asset_id UUID REFERENCES media_assets(id) ON DELETE SET NULL,
    category_name VARCHAR(80) NOT NULL,
    category_color VARCHAR(7) NOT NULL,
    category_position INTEGER NOT NULL,
    points INTEGER NOT NULL CHECK (points IN (10, 20, 30, 40, 50)),
    question_type VARCHAR(24) NOT NULL,
    prompt VARCHAR(1200) NOT NULL,
    answer VARCHAR(1200) NOT NULL,
    explanation VARCHAR(1600),
    used BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (session_id, category_position, points)
);

CREATE TABLE session_question_options (
    id UUID PRIMARY KEY,
    session_question_id UUID NOT NULL REFERENCES session_questions(id) ON DELETE CASCADE,
    text VARCHAR(600) NOT NULL,
    position INTEGER NOT NULL,
    correct BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (session_question_id, position)
);

CREATE TABLE score_events (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES game_sessions(id) ON DELETE CASCADE,
    team_id UUID NOT NULL REFERENCES session_teams(id),
    question_id UUID NOT NULL REFERENCES session_questions(id),
    points INTEGER NOT NULL,
    correct BOOLEAN NOT NULL,
    active_team_before INTEGER NOT NULL,
    active_team_after INTEGER NOT NULL,
    undone BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_session_teams_session ON session_teams(session_id);
CREATE INDEX idx_session_questions_session ON session_questions(session_id);
CREATE INDEX idx_score_events_session ON score_events(session_id, created_at DESC);
