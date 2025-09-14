# Physics Chapters Data Setup Guide

## Importing Data to TiDB

To import the physics chapters data into your TiDB database, follow these steps:

### Option 1: Using MySQL Client

1. Connect to your TiDB database using the MySQL client:

```bash
mysql -h <tidb-host> -P 4000 -u <username> -p
```

2. Select your database:

```sql
USE your_database_name;
```

3. Run the SQL script:

```bash
mysql -h <tidb-host> -P 4000 -u <username> -p your_database_name < h:\Coding\TiDB_2\TiDB_AgentX__2025\backend\src\main\resources\import_physics_data.sql
```

### Option 2: Using Spring Boot Application

1. Add the following property to your `application.properties` file to automatically run the SQL script on startup:

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:import_physics_data.sql
```

2. Start your Spring Boot application, and the script will be executed automatically.

## Verifying Data Import

You can verify that the data has been successfully imported and can be fetched from the database using the following methods:

### Option 1: Using the API Endpoints

1. Start your Spring Boot application.

2. Access the verification endpoint in your browser or using a tool like Postman:

```
GET http://localhost:8080/api/physics/verify-connection
```

This endpoint will return:
- A success status
- The total number of chapters in the database
- A sample of up to 3 chapters

3. To view all chapters:

```
GET http://localhost:8080/api/physics/chapters
```

4. To search for specific chapters by title or summary:

```
GET http://localhost:8080/api/physics/chapters/search?title=motion
```
or
```
GET http://localhost:8080/api/physics/chapters/search?summary=energy
```

### Option 2: Using MySQL Client

Connect to your TiDB database and run:

```sql
SELECT * FROM physics_chapters;
```

You should see 25 rows of physics chapter data.

## Troubleshooting

If you encounter issues:

1. Verify your TiDB connection properties in `application.properties`
2. Check that the database user has sufficient privileges
3. Ensure the table structure matches the entity class
4. Look for errors in the application logs

## Next Steps

Once you've confirmed the data is successfully imported and can be fetched, you can:

1. Extend the API with additional search capabilities
2. Create a frontend to display the physics chapters
3. Add functionality to update or add new chapters