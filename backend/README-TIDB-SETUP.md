# TiDB Cloud Integration Guide

This guide explains how to connect your application to TiDB Cloud for vector search and data storage capabilities.

## TiDB Cloud Connection

The application is already configured to connect to TiDB Cloud using the following properties in `application.properties`:

```properties
# TiDB Cloud Connection Properties
spring.datasource.url=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/test?sslMode=VERIFY_IDENTITY
spring.datasource.username=3pe2joZpdwMwTTQ.root
spring.datasource.password=Ep45VbbORFXpB182
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# TiDB Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=30000
```

## Setting Up Your Own TiDB Cloud Account

If you want to use your own TiDB Cloud account, follow these steps:

1. Sign up for a TiDB Cloud account at [https://tidbcloud.com/](https://tidbcloud.com/)
2. Create a new TiDB Serverless cluster
3. Once your cluster is created, navigate to the "Connect" tab
4. Copy the connection details (host, port, username, password)
5. Update the `application.properties` file with your connection details

## Required Database Schema

The application expects a table structure for storing documents and their vector embeddings. You can create the required table using the following SQL:

```sql
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    content_vector BLOB,
    document_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FULLTEXT INDEX idx_content (content) WITH PARSER ngram
);
```

## Vector Search Capabilities

The application supports the following search capabilities:

1. **Full-text search**: Search documents by text content
2. **Vector search**: Search documents by vector similarity
3. **Combined search**: Search using both text and vector similarity

## API Endpoints

The following API endpoints are available for interacting with TiDB:

- `GET /api/tidb/search?sessionId={sessionId}&prompt={prompt}` - Search for documents using a prompt
- `GET /api/tidb/connection-info` - Get TiDB connection information
- `POST /api/tidb/documents` - Create a new document
- `GET /api/tidb/documents/text-search?searchText={searchText}` - Search documents by text

## Security Considerations

1. **Connection Security**: The connection to TiDB Cloud uses SSL/TLS encryption with `sslMode=VERIFY_IDENTITY`
2. **Credentials Management**: Consider using environment variables or a secure vault for storing credentials in production
3. **Access Control**: Implement proper authentication and authorization for API endpoints

## Troubleshooting

If you encounter connection issues:

1. Verify your TiDB Cloud cluster is running
2. Check that your IP address is allowed in the TiDB Cloud network access settings
3. Ensure your credentials are correct
4. Check the application logs for detailed error messages

## Additional Resources

- [TiDB Cloud Documentation](https://docs.pingcap.com/tidbcloud/)
- [Spring Boot JPA Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jpa-and-spring-data)