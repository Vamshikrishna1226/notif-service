# notif-service

A real-time notification service I built to get hands-on with Kafka and WebSockets. The idea is simple. Anything can publish a notification via REST, and all connected browsers receive it instantly without polling.

At work I use Kafka for event streaming but never got a chance to wire it end to end with WebSockets for live UI updates. This was me doing that properly.

## How it works

1. A client sends a POST request with a notification payload
2. The backend publishes it to a Kafka topic
3. The Kafka consumer picks it up, checks Redis to skip duplicates, then pushes it over WebSocket
4. The React frontend receives it live via STOMP and shows it in the feed

## Stack

**Backend:** Java 17, Spring Boot 3.x, Spring WebFlux, Kafka, Redis, WebSockets  
**Frontend:** React 18, TypeScript, STOMP over SockJS  
**Infrastructure:** Docker (Kafka, Zookeeper, Redis)

## Running locally

You need Java 17, Node.js 20 and Docker.
```bash
git clone https://github.com/Vamshikrishna1226/notif-service.git
cd notif-service

# start Kafka and Redis
docker-compose up -d

# run backend
cd backend
mvn spring-boot:run

# run frontend (new terminal)
cd frontend
npm install
npm start
```

Backend runs on port 8081, frontend on port 3000.

## API

**Publish a notification**
```
POST http://localhost:8081/api/notifications/publish
```
```json
{
  "type": "INFO",
  "title": "Something happened",
  "message": "More details here",
  "source": "my-service"
}
```

Type can be INFO, WARNING, ALERT or ERROR.

**Health check**
```
GET http://localhost:8081/api/notifications/health
```

## Deduplication

Redis is used to catch duplicate events. Each notification has a UUID. If the same ID comes in twice within 5 minutes, the second one is dropped before it reaches the WebSocket layer. Useful when Kafka retries delivery after a consumer failure.

To test it, publish directly to Kafka with the same ID twice and check the backend logs.

## WebSocket

Frontend connects to ws://localhost:8081/ws using SockJS and subscribes to /topic/notifications. Any notification that clears Kafka and the duplicate check gets pushed here automatically.

## Why Kafka in the middle

Could have skipped Kafka and pushed directly from the REST endpoint to WebSocket. But that loses durability. If the WebSocket push fails, the event is gone. With Kafka in the middle, messages are persisted and the consumer can retry. Also makes it easy to add more consumers later, like one that writes to a database or sends email alerts.