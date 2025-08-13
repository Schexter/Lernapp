-- Initial Data for Fachinformatiker Lernapp
-- Version: 2.0
-- Author: Hans Hahn

-- ========================================
-- Default Roles
-- ========================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_USER', 'Standard user role'),
    ('ROLE_ADMIN', 'Administrator role'),
    ('ROLE_INSTRUCTOR', 'Instructor role'),
    ('ROLE_MODERATOR', 'Moderator role')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- Default Permissions
-- ========================================

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action) VALUES
    ('user.read', 'Read user information', 'user', 'read'),
    ('user.write', 'Modify user information', 'user', 'write'),
    ('user.delete', 'Delete users', 'user', 'delete'),
    ('question.read', 'Read questions', 'question', 'read'),
    ('question.write', 'Create/modify questions', 'question', 'write'),
    ('question.delete', 'Delete questions', 'question', 'delete'),
    ('topic.read', 'Read topics', 'topic', 'read'),
    ('topic.write', 'Create/modify topics', 'topic', 'write'),
    ('topic.delete', 'Delete topics', 'topic', 'delete'),
    ('progress.read', 'View learning progress', 'progress', 'read'),
    ('progress.write', 'Modify learning progress', 'progress', 'write'),
    ('admin.access', 'Access admin panel', 'admin', 'access')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- Assign Permissions to Roles
-- ========================================

-- Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- User gets basic read permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' 
AND p.name IN ('user.read', 'question.read', 'topic.read', 'progress.read')
ON CONFLICT DO NOTHING;

-- Instructor gets content management permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_INSTRUCTOR' 
AND p.name IN ('user.read', 'question.read', 'question.write', 'topic.read', 'topic.write', 'progress.read')
ON CONFLICT DO NOTHING;

-- Moderator gets moderation permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_MODERATOR' 
AND p.name IN ('user.read', 'user.write', 'question.read', 'question.write', 'topic.read', 'progress.read')
ON CONFLICT DO NOTHING;

-- ========================================
-- Default Admin User
-- ========================================

-- Insert default admin user (password: Admin123!)
-- Password hash is for BCrypt: Admin123!
INSERT INTO users (username, email, password_hash, first_name, last_name, email_verified, active)
VALUES ('admin', 'admin@lernapp.de', '$2a$10$N9qo8uLOickgx2ZMRZoHyeIjZRWO9H8ySFqQ8rzL7QJqBj0VsT8Iy', 'Admin', 'User', true, true)
ON CONFLICT (username) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- ========================================
-- Sample Topics
-- ========================================

-- Insert main topics
INSERT INTO topics (name, description, difficulty_level, estimated_hours, sort_order) VALUES
    ('IT-Grundlagen', 'Grundlegende IT-Konzepte und Begriffe', 1, 20, 1),
    ('Programmierung', 'Programmiersprachen und Entwicklung', 2, 40, 2),
    ('Datenbanken', 'Datenbankdesign und SQL', 2, 30, 3),
    ('Netzwerktechnik', 'Netzwerkgrundlagen und Protokolle', 3, 35, 4),
    ('IT-Sicherheit', 'Sicherheitskonzepte und Schutzmaßnahmen', 3, 25, 5),
    ('Projektmanagement', 'Agile und klassische Methoden', 2, 20, 6),
    ('Betriebssysteme', 'Windows, Linux und mehr', 2, 25, 7),
    ('Webentwicklung', 'Frontend und Backend Technologien', 2, 35, 8)
ON CONFLICT DO NOTHING;

-- Insert subtopics for Programmierung
INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'Java Grundlagen', 'Einführung in Java', 2, id, 1
FROM topics WHERE name = 'Programmierung'
ON CONFLICT DO NOTHING;

INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'Python Basics', 'Python für Anfänger', 1, id, 2
FROM topics WHERE name = 'Programmierung'
ON CONFLICT DO NOTHING;

INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'JavaScript', 'Moderne JavaScript Entwicklung', 2, id, 3
FROM topics WHERE name = 'Programmierung'
ON CONFLICT DO NOTHING;

INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'C# und .NET', 'Microsoft Entwicklung', 3, id, 4
FROM topics WHERE name = 'Programmierung'
ON CONFLICT DO NOTHING;

-- Insert subtopics for Datenbanken
INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'SQL Grundlagen', 'Structured Query Language', 2, id, 1
FROM topics WHERE name = 'Datenbanken'
ON CONFLICT DO NOTHING;

INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'NoSQL Datenbanken', 'MongoDB, Redis & Co', 3, id, 2
FROM topics WHERE name = 'Datenbanken'
ON CONFLICT DO NOTHING;

INSERT INTO topics (name, description, difficulty_level, parent_id, sort_order)
SELECT 
    'Datenbankdesign', 'ER-Modelle und Normalisierung', 3, id, 3
FROM topics WHERE name = 'Datenbanken'
ON CONFLICT DO NOTHING;

-- ========================================
-- Sample Tags
-- ========================================

INSERT INTO tags (name, description, color) VALUES
    ('Grundlagen', 'Basis-Wissen', '#4CAF50'),
    ('Fortgeschritten', 'Erweiterte Konzepte', '#FF9800'),
    ('Prüfungsrelevant', 'Wichtig für die Abschlussprüfung', '#F44336'),
    ('Praxis', 'Praktische Übungen', '#2196F3'),
    ('Theorie', 'Theoretische Konzepte', '#9C27B0'),
    ('Aktuell', 'Aktuelle Technologien', '#00BCD4')
ON CONFLICT (name) DO NOTHING;

-- ========================================
-- Sample Learning Paths
-- ========================================

INSERT INTO learning_paths (
    name, 
    description, 
    short_description,
    difficulty_level, 
    estimated_hours,
    prerequisites,
    learning_objectives,
    target_audience,
    certification_available
) VALUES
(
    'Fachinformatiker Grundausbildung',
    'Kompletter Lernpfad für angehende Fachinformatiker',
    'Alle wichtigen Themen für die Ausbildung',
    1,
    200,
    'Grundlegende Computer-Kenntnisse',
    'Vorbereitung auf die IHK-Prüfung, Praxiswissen für den Berufsalltag',
    'Auszubildende Fachinformatiker',
    true
),
(
    'Web Development Bootcamp',
    'Von null zum Full-Stack Entwickler',
    'Lerne moderne Webentwicklung',
    2,
    120,
    'Grundlegende Programmierkenntnisse',
    'HTML/CSS, JavaScript, React, Node.js, Datenbanken',
    'Angehende Webentwickler',
    true
),
(
    'Netzwerk-Administrator',
    'Professionelle Netzwerkverwaltung',
    'Netzwerke planen, aufbauen und verwalten',
    3,
    80,
    'IT-Grundlagen, Betriebssystem-Kenntnisse',
    'TCP/IP, Routing, Switching, Netzwerksicherheit',
    'IT-Administratoren',
    false
)
ON CONFLICT DO NOTHING;