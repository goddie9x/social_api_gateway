# Social Media API Gateway

This repository hosts the API Gateway for a social media platform built with **Spring Cloud Gateway**. This gateway routes requests to specific services and applies JWT-based authentication for secure access, while providing support for cross-origin resource sharing (CORS) and centralized logging.

To view all services for this social media system, lets visit: `https://github.com/goddie9x?tab=repositories&q=social`

## Features

- **Reactive API Gateway**: Built on Spring WebFlux to support non-blocking, high-throughput applications.
- **JWT Authentication**: Manages JWT-based security for all requests, except for specific paths that are publicly accessible.
- **Service Discovery**: Integrated with Eureka for dynamic service registration and discovery.
- **CORS Configuration**: Configured for global CORS support, allowing cross-origin requests from any origin.
- **Centralized Logging**: Logs incoming requests and responses to improve observability and debugging.
- **Timeouts**: Configures connection and response timeouts to manage network latency and prevent hanging requests.

## Configuration (`application.yml`)
- **Note**: Every single attribute in this file just for test, if you want to deploy it by your own way so let's override or write new attributes on this file
### Server Configuration
- **Port**: `8765` - The API gateway listens on this port.

### JWT Token
- **Secret**: Utilizes a secure secret for signing and verifying JWT tokens.

### Spring Cloud Gateway Routes

This API Gateway is configured to route traffic to multiple microservices based on URL paths. Each service is accessed through a unique path predicate and follows the pattern `api/v1/<service_name>/`. Below are the service mappings:

| Service              | URI                   | Path Predicate                    |
|----------------------|-----------------------|-----------------------------------|
| User Service         | `lb://user-service`   | `/api/v1/users/**`               |
| Friend Service       | `lb://friend-service` | `/api/v1/friends/**`             |
| Message Service      | `lb://message-service`| `/api/v1/messages/**`            |
| Notification Service | `lb://notification-service` | `/api/v1/notifications/**`   |
| Blob Service         | `lb://blob-service`   | `/api/v1/blobs/**`               |
| Post Service         | `lb://post-service`   | `/api/v1/posts/**`               |
| Comment Service      | `lb://comment-service`| `/api/v1/comments/**`            |
| Group Service        | `lb://group-service`  | `/api/v1/groups/**`              |
| Page Service         | `lb://page-service`   | `/api/v1/pages/**`               |
| Reaction Service     | `lb://reaction-service` | `/api/v1/reactions/**`         |

### Global CORS Configuration

CORS is configured to allow requests from any origin (`*`) for any HTTP method and header. Headers `Access-Control-Allow-Credentials` and `Access-Control-Allow-Origin` are deduplicated for cleaner responses.

### Redis Cache

The gateway uses Redis for caching to enhance performance and manage sessions. Redis configuration:
- **Host**: `redis`
- **Port**: `6379`
- **Username**: `thisIsJustTheUser`
- **Password**: `thisIsJustTheTestPassword123`
### Excluded Paths

Certain paths are excluded from JWT authentication for public access:
- `/api/v1/users/login`
- `/api/v1/users/register`
- `/api/v1/users/refresh-token`

### Eureka Configuration

The gateway registers with the Eureka discovery server at `http://discovery-server:8761/eureka/`. It both registers itself with Eureka and fetches the registry for other services.

## Logging Configuration

Logging is set to debug for both Spring Cloud Gateway and WebClient, aiding in tracing and debugging.

To run the API Gateway in a Docker container, follow these steps:

### Running in Docker

1. **Build the JAR file**:
   Ensure the application JAR file is built with Maven:

   ```bash
   mvn clean install
   ```

2. **Create the Docker Image**:
   Build the Docker image using the Dockerfile in the project directory:

   ```bash
   docker build -t social-media-api-gateway .
   ```

3. **Run the Docker Container**:
   Start a container from the newly created image:

   ```bash
   docker run -p 8765:8765 --network your_network_name -e REDIS_HOST=redis -e EUREKA_SERVER=http://discovery-server:8761/eureka/ social-media-api-gateway
   ```

   Replace `your_network_name` with the name of the Docker network that includes your Redis and Eureka services. Ensure that environment variables `REDIS_HOST` and `EUREKA_SERVER` are set correctly to connect to these dependencies.

The gateway will now be accessible at `http://localhost:8765`.

To run the API Gateway along with the entire social media ecosystem using Docker Compose, follow these steps.

### Running with Docker Compose

1. **Clone the Utilities Repository**:
   Start by cloning the `social_utils` repository, which contains the necessary `docker-compose.yaml` example and utility configurations:

   ```bash
   git clone https://github.com/goddie9x/social_utils.git
   cd social_utils
   ```

2. **Build the Docker Images**:
   Ensure all service images are built by navigating to each service folder (e.g., `../../ApiGateway`) and running:

   ```bash
   mvn clean install
   ```

   Alternatively, you can let Docker Compose handle this by automatically building the images on the first run.

3. **Start Docker Compose**:
   Run the entire stack using Docker Compose to bring up all services, including the API Gateway, Eureka server, Redis, and other components:

   ```bash
   docker-compose up --build
   ```

   This will set up the containers, establish network connections, and ensure all dependencies are linked.

4. **Verify the Setup**:
   Once all services are running, the API Gateway will be available at `http://localhost:8765`, while the other services (e.g., Kafka, MongoDB, etc.) will be accessible through the `social-media-network` Docker network.

### Services Overview

The `docker-compose.yaml` file defines several key services:
- **Kafka**: Handles messaging between microservices.
- **MongoDB**: Database for storing application data.
- **Oracle, Postgres**: Databases for specific services.
- **Elasticsearch and Kibana**: For search and data visualization.
- **Discovery Server (Eureka)**: Service registry for load balancing.
- **API Gateway**: Routes client requests to backend microservices.
  
Each service is assigned a port and added to the `social-media-network` to facilitate inter-service communication.

### Useful Commands

- **Stop Containers**: Use `docker-compose down` to stop all services and remove the containers.
- **Restart Containers**: Run `docker-compose restart` to restart the services without rebuilding the images.

This setup enables seamless orchestration of the social media microservices with an API Gateway for managing external client requests.

## Contributing

Contributions are welcome. Please clone this repository and submit a pull request with your changes. Ensure that your changes are well-tested and documented.

## License

This project is licensed under the MIT License. See `LICENSE` for more details.