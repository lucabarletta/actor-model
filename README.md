# actor-model

### Prerequisites

- Maven capable IDE (Intellij IDEA) or Docker Desktop installed

### Build and start

1. Clone this project
2. Step into the project with `cd actor-model`
3. Run build and start application with IDE or with Docker: `docker build -t actor-model . ; docker run -p 8080:8080 actor-model`
4. Start with REST Request `curl --request POST \
--url http://localhost:8080/api/start`
5. Stop with `curl --request POST \
--url http://localhost:8080/api/stop \
--header 'Content-Type: application/json'`
