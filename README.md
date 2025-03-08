# API Service Project

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)

API 서비스 프로젝트는 Kotlin과 Spring Boot를 사용하여 개발된 확장성 높은 RESTful API 시스템입니다. 이 프로젝트는 분산 환경에서의 동시성 처리, 보안, 그리고 이벤트 기반 아키텍처를 구현하고 있습니다.

## 기술 스택

- **언어 및 프레임워크:** Kotlin, Spring Boot
- **보안:** JWT 인증, OAuth
- **데이터베이스:**
    - MySQL (계정 및 사용자 정보)
    - MongoDB (거래 내역 히스토리)
- **캐싱 및 분산 락:** Redis
- **메시지 브로커:** Apache Kafka
- **직렬화:** kotlinx-serialization-json

## 주요 기능 및 아키텍처

### 분산 환경에서의 동시성 처리

다중 인스턴스 환경에서 동작하는 시스템을 가정하여 설계했으며, Redis를 활용한 분산 락(Distributed Lock)을 구현하였습니다.

```kotlin
// Redis를 활용한 분산 락 예시
fun <T> invokeWithMutex(mutexKey: String, function : () -> T?) : T? {
    val lock = redissonClient.getLock(mutexKey)

    try {
        lock.lock(15, TimeUnit.SECONDS)
        return function.invoke()
    } catch (e : Exception) {
        throw CustomException(ErrorCode.FailedToInvokeWithMutex, e.toString())
    } finally {
        lock.unlock()
    }

}
```

### 캐싱 전략

Redis를 활용하여 데이터베이스 부하를 줄이고 응답 성능을 향상시켰습니다.

```kotlin
// 캐시 적용 예시
fun get(key : String) : String? {
    return template.opsForValue().get(key)
}

fun <T> get(
    key : String,
    kSerializer : (Any) -> T?
): T? {
    val value = template.opsForValue().get(key)

    value?.let {
        return kSerializer(it)
    } ?: run {
        return null
    }
}

fun setIfNotExists(key: String, value: String): Boolean {
    return template.opsForValue().setIfAbsent(key, value) ?: false
}
```

### 보안 아키텍처

#### JWT 인증 필터

Bearer 토큰 검증을 Security 레벨에서 수행하여 인증되지 않은 요청에 대해 빠른 차단과 리소스 절약을 구현했습니다.

```kotlin
override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
) {
    // JWT 검증 로직
    // 인증 실패 시 즉시 응답 반환하여 후속 로직 실행 방지
}
```

#### 확장 가능한 OAuth 구현

다양한 로그인 방식을 지원하기 위해 인터페이스 기반 설계를 적용했습니다.

```kotlin
interface OAuthService {
    val providerName: String
    fun getToken(code: String): OAuth2TokenResponse
    fun getUserInfo(accessToken: String): OAuth2UserResponse
}

interface OAuth2TokenResponse {
    val accessToken: String
}

interface OAuth2UserResponse {
    val id: String
    val email: String?
    val name: String?
}
```

### AOP 한계 극복

Spring AOP의 한계(PointCut 표현식의 컴파일 타임 검증 불가, Self-invocation 문제 등)를 극복하기 위한 커스텀 솔루션을 구현했습니다.
- logger, transaction
```kotlin
// Transactional 예시
interface TransactionRunner {
    fun <T> run(function: () -> T?): T?
    fun <T> readOnly(function: () -> T?): T?
    fun <T> runNewTransaction(function: () -> T?): T?
}

@Component
class TxAdvice(
    private val advice: TransactionRunner = Advice()
) {
    fun <T> run(function: () -> T?): T? = advice.run(function)
    fun <T> readOnly(function: () -> T?): T? = advice.readOnly(function)
    fun <T> runNewTransaction(function: () -> T?): T? = advice.runNewTransaction(function)

    @Component
    private class Advice : TransactionRunner {
        @Transactional
        override fun <T> run(function: () -> T?): T? = function()

        @Transactional(readOnly = true)
        override fun <T> readOnly(function: () -> T?): T? = function()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        override fun <T> runNewTransaction(function: () -> T?): T? = function()
    }
}
```

### 이벤트 기반 아키텍처

Kafka를 활용하여 결제 및 자금 이동에 관련된 이벤트를 처리합니다. 이를 통해 시스템 간 느슨한 결합과 확장성을 확보했습니다.

```kotlin
enum class Topics(
    val topic: String,
) {
    Transactions("transactions"),
}

@Component
class KafkaProducer(
    private val template : KafkaTemplate<String, Any>,
    private val logger: Logger = Logging.getLogger(KafkaProducer::class.java)
) {
    fun sendMessage(topic: String, message: Any, key: String? = null){

        val future = if (key != null) {
            template.send(topic, key, message)
        } else {
            template.send(topic, message)
        }

        future.whenComplete { result, ex ->
            if (ex == null) {
                logger.info("메시지 발행 성공 - " +
                        "message  : $message" +
                        "topic: ${result.recordMetadata.topic()}, " +
                        "partition: ${result.recordMetadata.partition()}, " +
                        "offset: ${result.recordMetadata.offset()}")
            } else {
                logger.error("메시지 발행 실패 - ${ex.message}", ex)
            }
        }
    }
}
```

### 데이터베이스 설계

- **MySQL**: 관계형 데이터가 필요한 계정 정보, 사용자 정보 등 저장
```mysql
CREATE TABLE account (
         ulid VARCHAR(26) PRIMARY KEY,
         user_ulid VARCHAR(26) NOT NULL,
         balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
         account_number VARCHAR(100) NOT NULL UNIQUE,
         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         deleted_at TIMESTAMP NULL DEFAULT NULL
);

CREATE TABLE user (
      ulid VARCHAR(26) PRIMARY KEY,           -- ULID를 PK로 사용
      platform VARCHAR(25) NOT NULL,
      username VARCHAR(50) NOT NULL UNIQUE,  -- 사용자 이름 (유니크 제약 유지)
      access_token VARCHAR(255),             -- 액세스 토큰
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

- **MongoDB**: 트랜잭션 히스토리와 같은 주로 단일 조회 패턴을 가진 데이터 저장
```kotlin
@Serializable
@Document(collection = "transfer_history")
data class HistoryDoc (
    val fromUlid : String,
    val toUlid : String,

    @Serializable(with = BigDecimalSerializer::class)
    val value : BigDecimal,
    @Serializable(with = LocalDateTimeSerializer::class)
    val time : LocalDateTime
) {
    fun toHistory(fromUser : String, toUser : String): History = History(
        fromUser = fromUser,
        toUser = toUser,
        fromUlid = fromUlid,
        toUlid = toUlid,
        value = value,
        time = time,
    )
}

```


## 개발 환경 설정
```yaml
jwt:
  secret-key: <key>
  token-time-for-minute : 30

oauth2:
  providers:
    google:
      client-id: <client-id>
      client-secret: <secret>
      redirect-uri: <redirect-url>
    github:
      client-id: <client-id>
      client-secret: <secret>
      redirect-uri: <redirect-url>

database:
  redis:
    host: localhost
    port: 6379
    password: ""
    database: 0
    timeout: 10000
    thread-pool: 10
  redisson:
    host: <url>
    timeout: 10000
    password: ""
  mongo:
    uri: <url>
  mysql:
    url: jdbc:mysql://localhost:3306/bank?useSSL=false&serverTimezone=UTC
    username: root
    password:
    driver-class-name: com.mysql.cj.jdbc.Driver

spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
  kafka:
    bootstrap-servers: localhost:9092


global:
  client-auth-success-url: <redirect url>
```


### 필수 요구사항

- JDK 17 이상
- Docker 및 Docker Compose
- Redis
- Kafka
- MySQL
- MongoDB

### 로컬 개발 환경 세팅

```bash
# 필요한 인프라 컨테이너 실행
docker-compose up -d

# 애플리케이션 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```