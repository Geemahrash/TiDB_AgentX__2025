-- Create the QnA table with B+ tree indexes
CREATE TABLE IF NOT EXISTS qna_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    keywords VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create B+ tree indexes for efficient searching
-- Index on the question column for full text search
CREATE INDEX IF NOT EXISTS idx_question ON qna_entry (question(100));

-- Index on the keywords column for tag-based search
CREATE INDEX IF NOT EXISTS idx_keywords ON qna_entry (keywords);

-- Note: TiDB uses B+ tree indexes by default for these indexes
-- The (100) in question index limits the index to the first 100 characters
-- since TEXT fields require a length specification for indexes