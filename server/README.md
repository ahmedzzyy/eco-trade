# EcoTrade Backend

A Spring Boot backend for EcoTrade, a local marketplace for sustainable trading.

Note: *Production specific code are commented out as this is just a development project*

## Prerequisites

- Java 17+
- PostgreSQL (or Docker for containerized setup)
- Elasticsearch 8+
- Apache Kafka (for notifications)

## Setup

1. **Configure the environment**

    - Copy [`.env.example`](./.env.example) to `.env` and update values or your preferred way of setting environment variables.
    - Ensure PostgreSQL, Elasticsearch, and Kafka are running.
    - `.env` variables are used for development only.

2. **Run the application**

   ```sh
   ./gradlew bootRun
   ```

## 📖 API Documentation with OpenAPI (Swagger)

This project includes **OpenAPI (Swagger)** integration to provide an interactive and comprehensive API documentation experience.

- **Auto-generated API Docs** – Documentation is created dynamically.
- **Interactive Swagger UI** – Test API endpoints visually at `/swagger-ui.html`.
- **Detailed Endpoint Descriptions** – Includes request parameters, responses, and metadata.
- **JSON/YAML API Docs** – Available at `/api-docs`.

## Key Features

- **User Listings**: CRUD operations for marketplace listings
- **Elasticsearch**: Full-text search for listings
- ~~**Kafka Notifications**: Real-time notifications for interested listings~~
- **Security**: JWT-based authentication