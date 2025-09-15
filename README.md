# TiDB4 Application with B+ Tree Search

This application demonstrates the use of  B+ tree indexing capabilities for efficient question-answer data retrieval.

## Features

- B+ tree indexed search for fast data retrieval
- Question-answer database with keyword support
- REST API for managing QnA entries
- CSV data import capability
- React frontend with search interface

## Running the Application with Docker Compose

To get the application up and running, follow these simple steps:

1. Navigate to the root directory of the project.

2. Execute the following command in your terminal:
   ```bash
   docker-compose up --build
   ```
   This command will build the necessary images and start two containers: one for the frontend and one for the backend.

## Fixing Backend Connection Issues

1. **If the frontend shows “Backend Not Connected”, simply run the provided batch file:**
   ```
   ./run-backend.bat
   ```
   This script will:

Create a bridge container that resolves database connection conflicts between the backend and TiDB.

Establish a stable link so the Spring Boot backend can communicate properly with the database.

Ensure the frontend can connect to the backend without errors.
 Recommendation:
Keep this extra container running in the background while developing. It’s outside your main docker-compose setup and can be stopped later if needed, but keeping it running during development ensures everything works smoothly..

2. **Start the Frontend**
   ```
   cd frontend
   npm install (first time only)
   npm start
   ```
   This will start the React frontend on port 3000.

## Accessing the Application

Once the application is running, you can access it through your browser at the following addresses:

- Frontend: http://localhost:3000
- Backend: http://localhost:8081

## Using the B+ Tree Search

1. Navigate to the TiDB Search tab in the application
2. Enter your search query
3. The system will use B+ tree indexes to efficiently search the database
4. Results are ranked by relevance and displayed in the UI

## Adding Data

You can add question-answer data in several ways:

1. **CSV Import**: Use the `/api/qna/import/csv` endpoint to import data from a CSV file
2. **REST API**: Use the QnA REST endpoints to add, update, or delete entries
3. **Sample Data**: The application loads sample data from `sample-qna-data.csv` on startup

## API Endpoints

- `GET /api/qna` - List all QnA entries
- `GET /api/qna/{id}` - Get a specific QnA entry
- `POST /api/qna` - Create a new QnA entry
- `PUT /api/qna/{id}` - Update a QnA entry
- `DELETE /api/qna/{id}` - Delete a QnA entry
- `GET /api/qna/search?q={query}` - Search QnA entries
- `POST /api/qna/import/csv` - Import QnA entries from CSV

## B+ Tree Implementation

The application uses TiDB's B+ tree indexing for efficient data retrieval. The implementation includes:

1. **Indexed Fields**: Questions and keywords are indexed for fast search
2. **Keyword Extraction**: The system extracts keywords from queries for better matching
3. **Relevance Ranking**: Results are ranked based on match quality
