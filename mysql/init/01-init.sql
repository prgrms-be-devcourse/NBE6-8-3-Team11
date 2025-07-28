-- PetMatching Database Initialization Script

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS petmatching 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE petmatching;

-- Create user if not exists and grant privileges
CREATE USER IF NOT EXISTS 'petmatching_user'@'%' IDENTIFIED BY 'secure_user_password_change_me';
GRANT ALL PRIVILEGES ON petmatching.* TO 'petmatching_user'@'%';
FLUSH PRIVILEGES;

-- Create a test table to verify connection (will be replaced by JPA)
CREATE TABLE IF NOT EXISTS health_check (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(10) DEFAULT 'OK',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO health_check (status) VALUES ('OK');

-- Print success message
SELECT 'PetMatching database initialized successfully!' as message;