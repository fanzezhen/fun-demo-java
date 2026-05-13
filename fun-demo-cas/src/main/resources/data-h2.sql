-- H2 数据库初始数据
-- 插入测试用户（密码使用 BCrypt 加密）

-- 用户：admin，密码：admin123
-- BCrypt 加密后的密码（需要在实际使用时替换为真实的 BCrypt 哈希值）
INSERT INTO sys_user (username, password, email, phone, enabled) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '13800138000', TRUE);

-- 用户：test，密码：test123
INSERT INTO sys_user (username, password, email, phone, enabled) VALUES
('test', '$2a$10$5Z9Y7XqN5yqPqK8tGzE5OuRs7vQ8xWzE5OuRs7vQ8xWzE5OuRs7vQ', 'test@example.com', '13800138001', TRUE);

-- 用户：casuser，密码：Mellon
INSERT INTO sys_user (username, password, email, phone, enabled) VALUES
('casuser', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'casuser@example.com', '13800138002', TRUE);
