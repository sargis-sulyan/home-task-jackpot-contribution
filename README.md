# Jackpot Contribution and Reward Service

Backend home assignment implementation for processing jackpot bet contributions and jackpot reward evaluations.

The service receives bets through a REST API, publishes them to Kafka, consumes bet events from the `jackpot-bets` topic, contributes each bet to a matching jackpot pool, and exposes an API to evaluate whether a contributed bet wins a jackpot reward.

---

## Features

- Publish bet requests through REST API.
- Publish bet events to Kafka topic `jackpot-bets`.
- Consume bet events from Kafka.
- Contribute each consumed bet to a matching jackpot pool.
- Support fixed percentage contribution.
- Support variable percentage contribution.
- Evaluate contributed bets for jackpot reward.
- Support fixed chance reward.
- Support variable chance reward.
- Reset jackpot pool to initial amount when reward is won.
- Store bets, jackpots, contributions, and rewards in H2 in-memory database.
- Provide read-only endpoints for easier manual verification.
- Return structured JSON API errors.
- Use idempotency protection for duplicated bet processing and reward evaluation.

---

## Tech Stack

- Java 21
- Spring Boot 3.x
- Gradle Groovy
- Spring Web MVC
- Spring Kafka
- Spring Data JPA / Hibernate
- H2 in-memory database
- Docker Compose
- Apache Kafka
- Spring Boot Actuator
- Jakarta Bean Validation
- JUnit 5

---

## Business Flow

### Bet Publishing Flow

```text
Client
  -> POST /api/v1/bets
  -> Spring Boot REST API
  -> Kafka topic: jackpot-bets
```

### Contribution Flow

```text
Kafka topic: jackpot-bets
  -> Kafka consumer
  -> Find jackpot by jackpotId
  -> Calculate contribution
  -> Increase jackpot pool
  -> Save jackpot contribution
```

### Reward Evaluation Flow

```text
Client
  -> POST /api/v1/bets/{betId}/reward-evaluation
  -> Find contribution by betId
  -> Calculate winning chance
  -> Save reward if won
  -> Reset jackpot pool if won
```

---

## Domain Model

### Bet

A bet contains:

```json
{
  "betId": "bet-001",
  "userId": "user-001",
  "jackpotId": "fixed-jackpot",
  "betAmount": 100.00
}
```

### Jackpot Contribution

A jackpot contribution stores:

- Bet ID
- User ID
- Jackpot ID
- Stake amount
- Contribution amount
- Current jackpot amount after contribution
- Created date
- Reward evaluation status

### Jackpot Reward

A jackpot reward stores:

- Bet ID
- User ID
- Jackpot ID
- Jackpot reward amount
- Created date

---

## Contribution Strategies

### 1. Fixed Percentage Contribution

A fixed percentage of the bet amount is added to the jackpot pool.

```text
Bet amount: 100
Contribution percentage: 5%
Contribution amount: 5
```

### 2. Variable Percentage Contribution

The contribution percentage starts higher and decreases as the jackpot pool grows.

```text
poolGrowth = currentPoolAmount - initialPoolAmount
steps = poolGrowth / variableContributionStepAmount
currentPercentage = initialPercentage - steps * decreasePerStep
finalPercentage = max(currentPercentage, minPercentage)
contributionAmount = betAmount * finalPercentage / 100
```

---

## Reward Strategies

### 1. Fixed Chance Reward

The bet has a fixed chance to win the current jackpot pool.

```text
Reward chance: 10%
```

### 2. Variable Chance Reward

The winning chance starts lower and increases as the jackpot pool grows.

If the jackpot pool reaches the configured guaranteed amount, the chance becomes `100%`.

```text
if currentPoolAmount >= rewardGuaranteedAtAmount:
    chance = 100%

else:
    poolGrowth = currentPoolAmount - initialPoolAmount
    steps = poolGrowth / variableRewardStepAmount
    chance = initialChance + steps * increasePerStep
    chance = min(chance, 100%)
```

---

## Project Structure

```text
src/main/java
  └── .../jackpot
      ├── api
      │   ├── BetController
      │   ├── JackpotQueryController
      │   ├── GlobalExceptionHandler
      │   └── dto
      ├── application
      │   ├── BetApplicationService
      │   ├── JackpotContributionService
      │   ├── JackpotRewardEvaluationService
      │   └── JackpotQueryService
      ├── domain
      │   ├── model
      │   ├── strategy
      │   └── exception
      ├── infrastructure
      │   ├── kafka
      │   └── persistence
      └── config
```

---

## Design Decisions

### Strategy Pattern

Contribution and reward calculations are implemented through strategy interfaces.

This makes it easy to add new contribution or reward configuration types without rewriting the main service logic.

### Idempotency

The same `betId` cannot contribute more than once.

If Kafka redelivers the same bet event, the service returns the existing contribution instead of increasing the jackpot pool again.

Reward evaluation is also idempotent. The same bet cannot be evaluated repeatedly until it wins.

### Monetary Values

All monetary values use `BigDecimal`.

No `double` or `float` is used for money calculations.

### Transactional Consistency

Contribution processing is transactional.

Reward evaluation is transactional.

If a reward is won, saving the reward and resetting the jackpot happen in the same transaction.

### Concurrency

`JackpotEntity` uses optimistic locking with `@Version`.

This helps detect concurrent updates to the same jackpot pool.

### Kafka Topic Creation

Docker Compose starts Kafka.

The Spring Boot application declares the required Kafka topic using a `NewTopic` bean.

This avoids fragile shell-based topic initialization scripts.

---

## Prerequisites

You need:

- Java 21
- Docker
- Docker Compose
- Git

Check Java version:

```bash
java -version
```

---

## Running Kafka

Start Kafka:

```bash
docker compose up -d
```

Check container status:

```bash
docker compose ps
```

Expected result:

```text
jackpot-kafka   running
```

The Kafka topic is created by the Spring Boot application when the application starts.

Required topic:

```text
jackpot-bets
```

After starting the Spring Boot application, verify the topic:

```bash
docker exec -it jackpot-kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --list
```

Expected output:

```text
jackpot-bets
```

---

## Running the Application

Build the project:

```bash
./gradlew clean build
```

Run the application:

```bash
./gradlew bootRun
```

The application starts on:

```text
http://localhost:8080
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

---

## H2 Database Console

H2 console is available at:

```text
http://localhost:8080/h2-console/
```

Connection settings:

```text
JDBC URL: jdbc:h2:mem:jackpotdb
User Name: sa
Password:
```

Password is empty.

---

## Initial Jackpot Data

The application starts with two sample jackpots.

### Fixed Jackpot

```text
jackpotId: fixed-jackpot
initialPoolAmount: 1000.0000
currentPoolAmount: 1000.0000
contributionType: FIXED_PERCENTAGE
fixedContributionPercentage: 5%
rewardType: FIXED_CHANCE
fixedRewardChancePercentage: 10%
```

### Variable Jackpot

```text
jackpotId: variable-jackpot
initialPoolAmount: 1000.0000
currentPoolAmount: 1000.0000
contributionType: VARIABLE_PERCENTAGE
rewardType: VARIABLE_CHANCE
```

---

## API Usage

### 1. Publish Bet

Publishes a bet to Kafka topic `jackpot-bets`.

```http
POST /api/v1/bets
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "bet-001",
    "userId": "user-001",
    "jackpotId": "fixed-jackpot",
    "betAmount": 100.00
  }'
```

Example response:

```json
{
  "betId": "bet-001",
  "status": "PUBLISHED",
  "publishedAt": "2026-05-01T08:00:00Z"
}
```

After the message is published, the Kafka consumer processes the bet asynchronously and creates a jackpot contribution.

---

### 2. Get Jackpot

```http
GET /api/v1/jackpots/{jackpotId}
```

Example:

```bash
curl http://localhost:8080/api/v1/jackpots/fixed-jackpot
```

Example response:

```json
{
  "jackpotId": "fixed-jackpot",
  "initialPoolAmount": 1000.0000,
  "currentPoolAmount": 1005.0000,
  "contributionType": "FIXED_PERCENTAGE",
  "rewardType": "FIXED_CHANCE"
}
```

---

### 3. Get Contribution by Bet ID

```http
GET /api/v1/contributions/{betId}
```

Example:

```bash
curl http://localhost:8080/api/v1/contributions/bet-001
```

Example response:

```json
{
  "betId": "bet-001",
  "userId": "user-001",
  "jackpotId": "fixed-jackpot",
  "stakeAmount": 100.0000,
  "contributionAmount": 5.0000,
  "currentJackpotAmount": 1005.0000,
  "rewardEvaluated": false,
  "rewardEvaluatedAt": null,
  "createdAt": "2026-05-01T08:00:01Z"
}
```

---

### 4. Evaluate Reward

Evaluates whether a contributed bet wins the jackpot reward.

```http
POST /api/v1/bets/{betId}/reward-evaluation
```

Example:

```bash
curl -X POST http://localhost:8080/api/v1/bets/bet-001/reward-evaluation
```

Example response if the bet loses:

```json
{
  "betId": "bet-001",
  "userId": "user-001",
  "jackpotId": "fixed-jackpot",
  "won": false,
  "rewardAmount": 0,
  "currentJackpotAmount": 1005.0000,
  "winningChancePercentage": 10.0000,
  "evaluatedAt": "2026-05-01T08:00:10Z"
}
```

Example response if the bet wins:

```json
{
  "betId": "bet-001",
  "userId": "user-001",
  "jackpotId": "fixed-jackpot",
  "won": true,
  "rewardAmount": 1005.0000,
  "currentJackpotAmount": 1000.0000,
  "winningChancePercentage": 10.0000,
  "evaluatedAt": "2026-05-01T08:00:10Z"
}
```

If the bet wins, the jackpot pool is reset to the initial pool amount.

---

### 5. Get Reward by Bet ID

If the bet won, the reward can be retrieved by `betId`.

```http
GET /api/v1/rewards/{betId}
```

Example:

```bash
curl http://localhost:8080/api/v1/rewards/bet-001
```

Example response:

```json
{
  "betId": "bet-001",
  "userId": "user-001",
  "jackpotId": "fixed-jackpot",
  "jackpotRewardAmount": 1005.0000,
  "createdAt": "2026-05-01T08:00:10Z"
}
```

If the bet did not win, this endpoint returns `404`.

---

## Error Response Format

Validation and business errors are returned in a consistent format.

Example validation error:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Request validation failed",
  "status": 400,
  "path": "/api/v1/bets",
  "timestamp": "2026-05-01T08:00:00Z",
  "validationErrors": {
    "betId": "must not be blank",
    "betAmount": "must be greater than 0"
  }
}
```

Example not found error:

```json
{
  "code": "CONTRIBUTION_NOT_FOUND",
  "message": "Contribution not found for betId: unknown-bet",
  "status": 404,
  "path": "/api/v1/bets/unknown-bet/reward-evaluation",
  "timestamp": "2026-05-01T08:00:00Z",
  "validationErrors": {}
}
```

---

## Testing

Run all tests:

```bash
./gradlew test
```

Run full build:

```bash
./gradlew clean build
```

Planned test coverage:

- Contribution strategy calculations
- Reward strategy calculations
- Contribution processing
- Reward evaluation
- REST API validation and error handling
- Kafka producer/consumer flow

---

## Manual End-to-End Flow

Start Kafka:

```bash
docker compose up -d
```

Run the app:

```bash
./gradlew bootRun
```

Publish a bet:

```bash
curl -X POST http://localhost:8080/api/v1/bets \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "bet-e2e-001",
    "userId": "user-001",
    "jackpotId": "fixed-jackpot",
    "betAmount": 100.00
  }'
```

Wait one second for the Kafka consumer to process the message.

Check contribution:

```bash
curl http://localhost:8080/api/v1/contributions/bet-e2e-001
```

Evaluate reward:

```bash
curl -X POST http://localhost:8080/api/v1/bets/bet-e2e-001/reward-evaluation
```

Check jackpot state:

```bash
curl http://localhost:8080/api/v1/jackpots/fixed-jackpot
```

---

## Stopping the Application

Stop Spring Boot with:

```text
Ctrl + C
```

Stop Kafka:

```bash
docker compose down
```

Remove Kafka data/volumes if needed:

```bash
docker compose down -v
```

---

## Future Improvements

Possible improvements for a production-grade system:

- Add dead-letter topic for failed Kafka records.
- Add integration tests with Testcontainers Kafka.
- Add database migrations with Flyway.
- Replace H2 with PostgreSQL for production.
- Add OpenAPI/Swagger documentation.
- Add metrics for contributions and rewards.
- Add distributed tracing.
- Add authentication/authorization.
- Add jackpot configuration management APIs.
