-- TiDB Schema for Document Storage and Vector Search

-- Create documents table with vector support
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    content_vector BLOB,
    document_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FULLTEXT INDEX idx_content (content) WITH PARSER ngram
);

-- Create a function for cosine similarity calculation
-- Note: This is a placeholder. TiDB Cloud may provide built-in vector functions
-- or you may need to implement this differently based on TiDB's capabilities
DELIMITER //
CREATE FUNCTION IF NOT EXISTS COSINE_SIMILARITY(vector1 BLOB, vector2 BLOB) 
RETURNS FLOAT
DETERMINISTIC
BEGIN
    -- This is a placeholder for the actual implementation
    -- In a real implementation, you would unpack the BLOBs and calculate cosine similarity
    RETURN 1.0;
END //
DELIMITER ;