# LeetCode API Microservice

A standalone Spring Boot microservice that provides REST endpoints to fetch LeetCode statistics.

## Project Structure

- `com.leetcode.api.controller`: REST endpoints
- `com.leetcode.api.service`: Business logic for fetching LeetCode data and managing targets
- `com.leetcode.api.model`: Data transfer objects (DTOs)

## API Endpoints

### Get User Stats
- **URL**: `/api/stats`
- **Method**: `GET`
- **Query Params**: `username` (required)
- **Response**: `LeetCodeStats` JSON object

### Get Language Stats
- **URL**: `/api/language-stats`
- **Method**: `GET`
- **Query Params**: `username` (required)
- **Response**: List of `LanguageProgress` JSON objects

## How to Run

1.  **Build the project**:
    ```bash
    mvn clean install
    ```
2.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on port `8081`.

## Configuration

- `src/main/resources/application.properties`: Contains the server port configuration.
- `targets.json`: Stores problem-solving targets (generated on first run if not present).
