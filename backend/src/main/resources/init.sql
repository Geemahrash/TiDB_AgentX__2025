-- Initialize database
CREATE DATABASE IF NOT EXISTS mydatabase;

USE mydatabase;

-- Create a test table to verify database functionality
CREATE TABLE IF NOT EXISTS test_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert a test record
INSERT INTO test_table (name) VALUES ('Test Record');