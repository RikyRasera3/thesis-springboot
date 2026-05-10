# Spring Boot Thesis Comparator

REST backend built with Spring Boot, Java, Spring Web MVC, Spring Data JPA, and PostgreSQL for architectural and functional
comparison with the [Node.js](https://github.com/RikyRasera3/thesis-nodejs) application.

## Purpose

The application exposes a set of endpoints for account management and server status checks. Its behavior will remain 
aligned with the Node.js counterpart so that structure, logic, performance, and output can be compared consistently.

## Technology Stack

- [Java 26](https://www.oracle.com/news/announcement/oracle-releases-java-26-2026-03-17/)
- [Spring Boot 4](https://spring.io/projects/release-highlights)
- [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Validation](https://docs.spring.io/spring-framework/reference/core/validation.html)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL JDBC driver](https://spring.io/projects/spring-data-jdbc)
- [Lombok](https://projectlombok.org/)

## Prerequisites

- Java 26 and Gradle for local development
- Docker for containerized execution

## Available Gradle Tasks

- `./gradlew bootRun`: Starts the application in development mode
- `./gradlew build`: Compiles, tests, and packages the application
- `./gradlew test`: Runs the test suite
- `./gradlew bootJar`: Builds the executable Spring Boot jar

## Database Notes

Refer to the [README.md](database/README.md) file of `database/` folder for database setup

## Docker

The Spring Boot module includes both a `Dockerfile` and a `docker-compose.yml`.
The Compose setup starts the `postgres` service and the `app` service, which builds the `thesis-springboot-app` image and runs the `thesis-springboot-app`
container from [Dockerfile](Dockerfile).
Use these commands from the `thesis-springboot/` directory to manage the Docker setup:

### Build the image

```bash
docker compose build
```

### Start the container

```bash
docker compose up -d
```

### Stop and remove the container

```bash
docker compose down
```

## Project Structure

```text
src/
  main/
    java/it/thesis/springboot/
      config/        Spring Security configuration
      controller/    REST controllers
      dto/           Request/response DTOs and search criteria
      factory/       DTO-to-entity update helpers
      model/         JPA entities
      repository/    Spring Data repositories
      service/       Application logic and specifications
    resources/       Application configuration
```

## Run in Development

Start the application directly with Gradle:

```bash
./gradlew bootRun
```

## Build and Run

Build the project:

```bash
./gradlew build
```

Run the packaged application:

```bash
java -jar build/libs/*.jar
```

## Run Tests

```bash
./gradlew test
```

## Quick Service Check

Health endpoint:

```bash
curl http://localhost:8080/server
```

Expected response:

```json
{
  "status": "OK",
  "message": "Server is running"
}
```

Ping endpoint:

```bash
curl http://localhost:8080/server/ping
```

Expected response:

```json
{
  "message": "pong"
}
```

## Available APIs

### Server

- `GET /server`
- `GET /server/ping`

### Account

- `GET /accounts/search`
- `GET /accounts`
- `GET /accounts/{id}`
- `POST /accounts`
- `PATCH /accounts/{id}`
- `DELETE /accounts/{id}`

## Payloads and Parameters

### `GET /accounts/search`

Supported query parameters:

- `page`: page index, default `0`
- `size`: page size, default `10`
- `roleIds`: a single role id or a comma-separated list, for example `1` or `1,2,3`

Example:

```bash
curl "http://localhost:8080/accounts/search?page=0&size=10&roleIds=1,2"
```

Note: the `totalPages` field is calculated using `Math.ceil(totalElements / size)`.

### `GET /accounts`

Supported query parameters:

- `roleIds`: a single role id or a comma-separated list

Example:

```bash
curl "http://localhost:8080/accounts?roleIds=1,2"
```

### `GET /accounts/{id}`

Example:

```bash
curl http://localhost:8080/accounts/1
```

### `POST /accounts`

Required body:

```json
{
  "name": "Mario",
  "surname": "Rossi",
  "email": "mario.rossi@example.com",
  "phone": "+390123456789",
  "dateOfBirth": "1995-05-20"
}
```

Example:

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mario",
    "surname": "Rossi",
    "email": "mario.rossi@example.com",
    "phone": "+390123456789",
    "dateOfBirth": "1995-05-20"
  }'
```

### `PATCH /accounts/{id}`

Required body:

```json
{
  "email": "mario.rossi.updated@example.com"
}
```

Example:

```bash
curl -X PATCH http://localhost:8080/accounts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "mario.rossi.updated@example.com"
  }'
```

### `DELETE /accounts/{id}`

Example:

```bash
curl -X DELETE http://localhost:8080/accounts/1
```

## Test Environment

Tests have been executed deploying the container into a
[Cloud Run](https://cloud.google.com/run?_gl=1*gpb51k*_up*MQ..&gclid=CjwKCAjwzLHPBhBTEiwABaLsSnKN_mvr7If9AkAgTHfVeFSFXSuNmwhm30SYU3zVQobJGYhkHR6H4hoCgqEQAvD_BwE&gclsrc=aw.ds)
instance of
[Google Cloud Platform](https://cloud.google.com/?_gl=1*1o4sew4*_up*MQ..&gclid=CjwKCAjw5NvPBhAoEiwA_2egfqYaZpsdEUY6ez7ypP25M9AE5FgZq5TuEzXZf3387FQGYbhOQmVs-xoC02cQAvD_BwE&gclsrc=aw.ds)
with the following configurations:

### Configurations

- 1 vCPU
- 512 MiB RAM
- 