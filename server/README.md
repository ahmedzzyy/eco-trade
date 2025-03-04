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

## Key Features

- **User Listings**: CRUD operations for marketplace listings
- **Elasticsearch**: Full-text search for listings
- ~~**Kafka Notifications**: Real-time notifications for interested listings~~
- **Security**: JWT-based authentication