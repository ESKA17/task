# Task Manager Application

## Overview

This Task Manager application is a Spring Boot REST service designed for managing tasks. It allows users to create,
retrieve, update, and delete tasks, as well as filter tasks based on their status and creation date. The application
uses H2 as an in-memory database and Liquibase for database schema management.

## Features

1. **Task Management**:
    - Create a new task with a title, description (optional), status (default: "NEW"), and an auto-generated ID.
    - Retrieve tasks with optional filtering by status and creation date.
    - Update an existing task (title, description, or status).
    - Delete a task by its ID.

2. **Data Persistence**:
    - Stores task data in an H2 in-memory database.

3. **Validation**:
    - Uses Bean Validation to enforce constraints like non-empty titles and status.

4. **Error Handling**:
    - Returns appropriate HTTP status codes (e.g., `400 Bad Request`, `404 Not Found`).

## Requirements

- Java 21
- Maven

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
```

### 2. Build and Run the Application

Use Maven to build and start the application:

```bash
./mvnw clean install
cd task-service
./mvnw spring-boot:run
```

### 3. Access the Application

- **Base URL**: `http://localhost:8080`
- **Endpoints**:
    - `POST api/v1/tasks` - Create a task
    - `GET api/v1/tasks/filter` - Retrieve all tasks
    - `GET api/v1/tasks/{id}` - Retrieve a task by ID
    - `PUT api/v1/tasks/{id}` - Update a task
    - `DELETE api/v1/tasks/{id}` - Delete a task by ID

## Running Tests

Run the unit tests using Maven:

```bash
./mvnw test
```

## Docker Support (Optional)

To build and run the application using Docker:

1. **Build Docker Image**:
   ```bash
   docker build -t task-manager .
   ```

2. **Run the Docker Container**:
   ```bash
   docker run -p 8080:8080 task-manager
   ```

## Development Notes

- The project uses the latest version of Spring Boot and adheres to modern coding conventions.

## Future Enhancements

- Add authentication and authorization.
- Support for pagination and sorting in task retrieval.
- Deployment to cloud platforms like AWS or Azure.

