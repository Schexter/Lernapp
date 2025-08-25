-- Fachinformatiker Lernapp Database Schema
-- Version: 1.0 (PostgreSQL Production)
-- Author: Hans Hahn
-- Date: 2025-08-13

-- Enable UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ========================================
-- Core User Management Tables
-- ========================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    bio TEXT,
    learning_style VARCHAR(20) DEFAULT 'VISUAL',
    learning_streak INTEGER DEFAULT 0,
    total_points INTEGER DEFAULT 0,
    current_level INTEGER DEFAULT 1,
    learning_progress JSONB DEFAULT '{}',
    preferences JSONB DEFAULT '{}',
    last_login_at TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_user_active ON users(active);
CREATE INDEX IF NOT EXISTS idx_user_learning_progress ON users USING GIN (learning_progress);
CREATE INDEX IF NOT EXISTS idx_user_preferences ON users USING GIN (preferences);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_role_name ON roles(name);

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    resource VARCHAR(50),
    action VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_permission_name ON permissions(name);

-- User-Role junction table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role-Permission junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ========================================
-- Learning Content Tables
-- ========================================

-- Topics table (hierarchical)
CREATE TABLE IF NOT EXISTS topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    color_code VARCHAR(7),
    sort_order INTEGER DEFAULT 0,
    difficulty_level INTEGER DEFAULT 1,
    estimated_hours INTEGER,
    parent_id BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES topics(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_topic_parent ON topics(parent_id);
CREATE INDEX IF NOT EXISTS idx_topic_sort ON topics(sort_order);
CREATE INDEX IF NOT EXISTS idx_topic_active ON topics(active);

-- Questions table
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    question_text TEXT NOT NULL,
    question_code TEXT,
    image_url VARCHAR(500),
    question_type VARCHAR(20) NOT NULL DEFAULT 'MULTIPLE_CHOICE',
    difficulty_level INTEGER NOT NULL DEFAULT 1,
    points INTEGER DEFAULT 10,
    time_limit_seconds INTEGER,
    explanation TEXT,
    hint TEXT,
    reference_url VARCHAR(500),
    metadata JSONB,
    topic_id BIGINT NOT NULL,
    created_by BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_question_topic ON questions(topic_id);
CREATE INDEX IF NOT EXISTS idx_question_type ON questions(question_type);
CREATE INDEX IF NOT EXISTS idx_question_difficulty ON questions(difficulty_level);
CREATE INDEX IF NOT EXISTS idx_question_active ON questions(active);
CREATE INDEX IF NOT EXISTS idx_question_metadata ON questions USING GIN (metadata);

-- Answers table
CREATE TABLE IF NOT EXISTS answers (
    id BIGSERIAL PRIMARY KEY,
    answer_text TEXT NOT NULL,
    answer_code TEXT,
    image_url VARCHAR(500),
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    explanation TEXT,
    sort_order INTEGER DEFAULT 0,
    question_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_answer_question ON answers(question_id);
CREATE INDEX IF NOT EXISTS idx_answer_correct ON answers(is_correct);

-- Tags table
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    color VARCHAR(7),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_tag_name ON tags(name);

-- Question-Tag junction table
CREATE TABLE IF NOT EXISTS question_tags (
    question_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (question_id, tag_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- ========================================
-- Learning Progress Tables
-- ========================================

-- User Progress table
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    attempts INTEGER DEFAULT 0,
    correct_attempts INTEGER DEFAULT 0,
    last_attempt TIMESTAMP,
    next_review TIMESTAMP,
    confidence_level DECIMAL(5,2) DEFAULT 0.00,
    repetition_interval INTEGER DEFAULT 1,
    ease_factor DECIMAL(4,2) DEFAULT 2.5,
    consecutive_correct INTEGER DEFAULT 0,
    time_spent_seconds BIGINT DEFAULT 0,
    last_response_time_seconds INTEGER,
    mastery_level VARCHAR(20) DEFAULT 'NOT_STARTED',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE(user_id, question_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_progress_user_question ON user_progress(user_id, question_id);
CREATE INDEX IF NOT EXISTS idx_progress_next_review ON user_progress(next_review);
CREATE INDEX IF NOT EXISTS idx_progress_confidence ON user_progress(confidence_level);

-- Learning Paths table
CREATE TABLE IF NOT EXISTS learning_paths (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    icon_url VARCHAR(500),
    banner_url VARCHAR(500),
    difficulty_level INTEGER DEFAULT 1,
    estimated_hours INTEGER,
    prerequisites TEXT,
    learning_objectives TEXT,
    target_audience TEXT,
    certification_available BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    created_by BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_learning_path_active ON learning_paths(active);
CREATE INDEX IF NOT EXISTS idx_learning_path_level ON learning_paths(difficulty_level);

-- Learning Path Topics junction table
CREATE TABLE IF NOT EXISTS learning_path_topics (
    learning_path_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    sort_order INTEGER DEFAULT 0,
    PRIMARY KEY (learning_path_id, topic_id),
    FOREIGN KEY (learning_path_id) REFERENCES learning_paths(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- User Learning Paths (enrollment)
CREATE TABLE IF NOT EXISTS user_learning_paths (
    user_id BIGINT NOT NULL,
    learning_path_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    PRIMARY KEY (user_id, learning_path_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (learning_path_id) REFERENCES learning_paths(id) ON DELETE CASCADE
);

-- User Topics (preferences)
CREATE TABLE IF NOT EXISTS user_topics (
    user_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, topic_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- ========================================
-- Examination & Session Tables
-- ========================================

-- Examination Sessions table
CREATE TABLE IF NOT EXISTS examination_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_type VARCHAR(30) DEFAULT 'PRACTICE',
    topic_ids VARCHAR(500),
    total_questions INTEGER,
    answered_questions INTEGER DEFAULT 0,
    correct_answers INTEGER DEFAULT 0,
    score DECIMAL(5,2) DEFAULT 0.00,
    time_limit_minutes INTEGER,
    time_spent_seconds BIGINT DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'NOT_STARTED',
    session_data JSONB,
    results JSONB,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_exam_user ON examination_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_exam_status ON examination_sessions(status);
CREATE INDEX IF NOT EXISTS idx_exam_started ON examination_sessions(started_at);
CREATE INDEX IF NOT EXISTS idx_exam_session_data ON examination_sessions USING GIN (session_data);
CREATE INDEX IF NOT EXISTS idx_exam_results ON examination_sessions USING GIN (results);

-- Session Answers table
CREATE TABLE IF NOT EXISTS session_answers (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_answer_id BIGINT,
    text_answer TEXT,
    is_correct BOOLEAN,
    points_earned INTEGER DEFAULT 0,
    time_spent_seconds INTEGER,
    answered_at TIMESTAMP,
    marked_for_review BOOLEAN DEFAULT FALSE,
    confidence_level INTEGER,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (session_id) REFERENCES examination_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (selected_answer_id) REFERENCES answers(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_session_answer_session ON session_answers(session_id);
CREATE INDEX IF NOT EXISTS idx_session_answer_question ON session_answers(question_id);
