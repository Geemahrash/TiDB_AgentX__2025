# TiDB Cloud Integration

This project integrates with TiDB Cloud to provide vector search and full-text search capabilities for the AI application.

## Features

### 1. Ingest & Index Data

The application can ingest and index various types of data into TiDB Serverless:

- **Text Documents**: Store and index full-text content with FULLTEXT indexes
- **Vector Embeddings**: Convert text to vector embeddings for semantic search
- **Session-based Storage**: Associate data with specific user sessions

### 2. Search Capabilities

The application provides multiple search methods:

- **Vector Search**: Find semantically similar content using vector embeddings
- **Full-text Search**: Find keyword matches using MySQL-compatible FULLTEXT search
- **Hybrid Search**: Combine both approaches for comprehensive results

## API Endpoints

### Document Ingestion

```
POST /api/tidb/ingest
```

Request body:
```json
{
  "title": "Document Title",
  "content": "Document content to be indexed",
  "documentType": "article",
  "sessionId": "user-session-123"
}
```

### Search

```
GET /api/tidb/search?query=your+search+query&sessionId=user-session-123
```

The `sessionId` parameter is optional. If provided, it will prioritize results from that session.

### Get Documents by Session

```
GET /api/tidb/documents/{sessionId}
```

## Testing

The application includes test endpoints to verify TiDB Cloud connectivity:

```
GET /api/test/tidb-status
```

Add a sample document:
```
POST /api/test/add-sample
```

Test search functionality:
```
GET /api/test/search-test?query=your+test+query
```

## Database Schema

The application uses two main tables:

1. **documents**: Stores document metadata and content with FULLTEXT indexing
2. **vectors**: Stores vector embeddings linked to documents

The schema is automatically created when the application starts.

## Configuration

The TiDB Cloud connection is configured in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test?sslMode=VERIFY_IDENTITY
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## Sample Data

When running with the `dev` profile, the application automatically loads sample data for testing purposes.