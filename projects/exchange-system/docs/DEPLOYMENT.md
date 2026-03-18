# 🚀 TurboX Exchange - 部署文档

> 版本: 1.0.0
> 更新日期: 2024-12-29
> 状态: 草案

---

## 1. 环境要求

| 工具 | 最低版本 | 说明 |
| ---- | -------- | ---- |
| JDK | 21+ | LTS 版本，支持虚拟线程 |
| Maven | 3.9+ | 构建工具 |
| Node.js | 20+ | 前端构建 |
| Docker | 24+ | 容器运行时 |
| Docker Compose | 2.20+ | 本地编排 |

---

## 2. 基础设施依赖

| 组件 | 镜像 | 版本 | 默认端口 | 用途 |
| ---- | ---- | ---- | -------- | ---- |
| MySQL | mysql | 8.0 | 3306 | 主数据存储 |
| Redis | redis | 7.0 | 6379 | 缓存 / 分布式锁 / 限流 |
| Kafka | confluentinc/cp-kafka | 7.5.0 | 9092 | 事件驱动消息队列 |
| Zookeeper | confluentinc/cp-zookeeper | 7.5.0 | 2181 | Kafka 依赖 |
| Nacos | nacos/nacos-server | v2.3.0 | 8848 | 注册中心 + 配置中心 |
| MinIO | minio/minio | latest | 9000 / 9001 | 对象存储（快照备份） |
| Elasticsearch | elasticsearch | 8.x | 9200 | 日志 / 订单搜索（可选） |

---

## 3. 本地开发部署

### 3.1 一键启动基础设施

创建 `docker/docker-compose.infra.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: turbox-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123456
      MYSQL_DATABASE: turbox_exchange
      MYSQL_CHARACTER_SET_SERVER: utf8mb4
      MYSQL_COLLATION_SERVER: utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init-sql:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    command: --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7.0
    container_name: turbox-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    command: redis-server --requirepass redis123456

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: turbox-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    volumes:
      - zookeeper_data:/var/lib/zookeeper/data
    healthcheck:
      test: ["CMD", "echo", "ruok", "|", "nc", "localhost", "2181"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: turbox-kafka
    depends_on:
      zookeeper:
        condition: service_healthy
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"
    ports:
      - "9092:9092"
    volumes:
      - kafka_data:/var/lib/kafka/data
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 15s
      timeout: 10s
      retries: 5

  nacos:
    image: nacos/nacos-server:v2.3.0
    container_name: turbox-nacos
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: ""
      JVM_XMS: 256m
      JVM_XMX: 256m
    ports:
      - "8848:8848"
      - "9848:9848"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8848/nacos/actuator/health"]
      interval: 15s
      timeout: 10s
      retries: 5

  minio:
    image: minio/minio:latest
    container_name: turbox-minio
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin123
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
  redis_data:
  zookeeper_data:
  kafka_data:
  minio_data:
```

**启动命令**

```bash
cd docker
docker-compose -f docker-compose.infra.yml up -d

# 查看所有服务状态
docker-compose -f docker-compose.infra.yml ps

# 查看日志
docker-compose -f docker-compose.infra.yml logs -f kafka
```

### 3.2 服务启动顺序

后端服务需按依赖顺序启动：

| 顺序 | 服务 | 端口 | 说明 |
| ---- | ---- | ---- | ---- |
| 1 | exchange-common | - | 公共模块（仅 mvn install） |
| 2 | exchange-gateway | 8080 | API 网关 |
| 3 | exchange-user | 8081 | 用户服务 |
| 4 | exchange-account | 8082 | 账户服务 |
| 5 | exchange-order | 8083 | 订单服务 |
| 6 | exchange-match | 8084 | 撮合引擎 |
| 7 | exchange-market | 8085 | 行情服务 |
| 8 | exchange-risk | 8086 | 风控服务 |
| 9 | exchange-admin | 8087 | 管理后台 |

**启动步骤**

```bash
# 1. 安装公共模块
cd exchange-common
mvn clean install -DskipTests

# 2. 逐个启动服务（每个服务在独立终端中）
cd exchange-gateway
mvn spring-boot:run

cd exchange-user
mvn spring-boot:run

# ... 依次启动其他服务
```

或使用脚本：

```bash
./scripts/start-all.sh
```

### 3.3 前端启动

```bash
cd exchange-web
npm install
npm run dev
# 前端运行在 http://localhost:3000
```

### 3.4 环境变量清单

| 变量名 | 默认值 | 说明 | 使用服务 |
| ------ | ------ | ---- | -------- |
| DB_HOST | localhost | MySQL 地址 | 全部后端服务 |
| DB_PORT | 3306 | MySQL 端口 | 全部后端服务 |
| DB_NAME | turbox_exchange | 数据库名 | 全部后端服务 |
| DB_USER | root | 数据库用户 | 全部后端服务 |
| DB_PASS | root123456 | 数据库密码 | 全部后端服务 |
| REDIS_HOST | localhost | Redis 地址 | 全部后端服务 |
| REDIS_PORT | 6379 | Redis 端口 | 全部后端服务 |
| REDIS_PASSWORD | redis123456 | Redis 密码 | 全部后端服务 |
| KAFKA_BOOTSTRAP_SERVERS | localhost:9092 | Kafka 地址 | order, match, market, account |
| NACOS_SERVER_ADDR | localhost:8848 | Nacos 地址 | 全部后端服务 |
| WALLET_MODE | mock | 钱包模式（mock/real） | account |
| JWT_SECRET | (随机生成) | JWT 签名密钥 | gateway, user |
| JWT_EXPIRATION | 7200 | Access Token 有效期（秒） | gateway, user |
| MINIO_ENDPOINT | http://localhost:9000 | MinIO 地址 | match |
| MINIO_ACCESS_KEY | minioadmin | MinIO 访问密钥 | match |
| MINIO_SECRET_KEY | minioadmin123 | MinIO 密钥 | match |

---

## 4. Docker 全服务部署

### 4.1 Dockerfile 模板

各微服务使用统一的多阶段构建 Dockerfile：

```dockerfile
# 构建阶段
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# 先复制 pom.xml 利用 Docker 缓存
COPY pom.xml .
COPY exchange-common/pom.xml exchange-common/
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY . .
RUN mvn clean package -DskipTests -pl exchange-{service} -am

# 运行阶段
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 安全：使用非 root 用户
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /build/exchange-{service}/target/exchange-{service}-*.jar app.jar

EXPOSE {port}

ENTRYPOINT ["java", \
  "-XX:+UseG1GC", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
```

### 4.2 docker-compose.yml 全服务编排

```yaml
version: '3.8'

services:
  # ========== 基础设施 ==========
  mysql:
    image: mysql:8.0
    container_name: turbox-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASS:-root123456}
      MYSQL_DATABASE: ${DB_NAME:-turbox_exchange}
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init-sql:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7.0
    container_name: turbox-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --requirepass ${REDIS_PASSWORD:-redis123456}
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD:-redis123456}", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: turbox-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    healthcheck:
      test: ["CMD", "echo", "ruok", "|", "nc", "localhost", "2181"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: turbox-kafka
    depends_on:
      zookeeper:
        condition: service_healthy
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    healthcheck:
      test: ["CMD", "kafka-broker-api-versions", "--bootstrap-server", "localhost:9092"]
      interval: 15s
      timeout: 10s
      retries: 5

  nacos:
    image: nacos/nacos-server:v2.3.0
    container_name: turbox-nacos
    environment:
      MODE: standalone
    ports:
      - "8848:8848"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8848/nacos/actuator/health"]
      interval: 15s
      timeout: 10s
      retries: 5

  minio:
    image: minio/minio:latest
    container_name: turbox-minio
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY:-minioadmin}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY:-minioadmin123}
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"

  # ========== 后端服务 ==========
  gateway:
    build:
      context: ..
      dockerfile: docker/Dockerfile.gateway
    container_name: turbox-gateway
    depends_on:
      nacos:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"

  user:
    build:
      context: ..
      dockerfile: docker/Dockerfile.user
    container_name: turbox-user
    depends_on:
      mysql:
        condition: service_healthy
      nacos:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      DB_HOST: mysql
      DB_PASS: ${DB_PASS:-root123456}
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}
      JWT_SECRET: ${JWT_SECRET}

  account:
    build:
      context: ..
      dockerfile: docker/Dockerfile.account
    container_name: turbox-account
    depends_on:
      mysql:
        condition: service_healthy
      nacos:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      DB_HOST: mysql
      DB_PASS: ${DB_PASS:-root123456}
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      WALLET_MODE: ${WALLET_MODE:-mock}

  order:
    build:
      context: ..
      dockerfile: docker/Dockerfile.order
    container_name: turbox-order
    depends_on:
      mysql:
        condition: service_healthy
      nacos:
        condition: service_healthy
      kafka:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      DB_HOST: mysql
      DB_PASS: ${DB_PASS:-root123456}
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092

  match:
    build:
      context: ..
      dockerfile: docker/Dockerfile.match
    container_name: turbox-match
    depends_on:
      nacos:
        condition: service_healthy
      kafka:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY:-minioadmin}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY:-minioadmin123}

  market:
    build:
      context: ..
      dockerfile: docker/Dockerfile.market
    container_name: turbox-market
    depends_on:
      mysql:
        condition: service_healthy
      nacos:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      DB_HOST: mysql
      DB_PASS: ${DB_PASS:-root123456}
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092

  risk:
    build:
      context: ..
      dockerfile: docker/Dockerfile.risk
    container_name: turbox-risk
    depends_on:
      nacos:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD:-redis123456}

  admin:
    build:
      context: ..
      dockerfile: docker/Dockerfile.admin
    container_name: turbox-admin
    depends_on:
      mysql:
        condition: service_healthy
      nacos:
        condition: service_healthy
    environment:
      NACOS_SERVER_ADDR: nacos:8848
      DB_HOST: mysql
      DB_PASS: ${DB_PASS:-root123456}
    ports:
      - "8087:8087"

  # ========== 前端 ==========
  web:
    build:
      context: ../exchange-web
      dockerfile: Dockerfile
    container_name: turbox-web
    depends_on:
      - gateway
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_URL: http://gateway:8080

volumes:
  mysql_data:
  redis_data:
  minio_data:
```

### 4.3 一键构建部署

```bash
# 构建并启动所有服务
cd docker
docker-compose up -d --build

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f gateway

# 停止所有服务
docker-compose down

# 停止并清除数据卷
docker-compose down -v
```

---

## 5. Kubernetes 生产部署

### 5.1 命名空间与资源规划

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: turbox-exchange
```

**资源规划**

| 服务 | 副本数 | CPU Request | CPU Limit | Memory Request | Memory Limit |
| ---- | ------ | ----------- | --------- | -------------- | ------------ |
| gateway | 3 | 500m | 1000m | 512Mi | 1Gi |
| user | 3 | 500m | 1000m | 512Mi | 1Gi |
| account | 3 | 500m | 1000m | 512Mi | 1Gi |
| order | 3 | 500m | 1000m | 512Mi | 1Gi |
| match | 1/交易对 | 1000m | 2000m | 1Gi | 2Gi |
| market | 3 | 500m | 1000m | 512Mi | 1Gi |
| risk | 2 | 500m | 1000m | 512Mi | 1Gi |
| admin | 2 | 250m | 500m | 256Mi | 512Mi |

### 5.2 各服务部署配置要点

**无状态服务 (Deployment + HPA)**

适用于 gateway, user, account, order, market, risk, admin。

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: exchange-gateway
  namespace: turbox-exchange
spec:
  replicas: 3
  selector:
    matchLabels:
      app: exchange-gateway
  template:
    metadata:
      labels:
        app: exchange-gateway
    spec:
      containers:
        - name: gateway
          image: turbox/exchange-gateway:latest
          ports:
            - containerPort: 8080
          env:
            - name: NACOS_SERVER_ADDR
              valueFrom:
                configMapKeyRef:
                  name: turbox-config
                  key: nacos-server-addr
            - name: JWT_SECRET
              valueFrom:
                secretKeyRef:
                  name: turbox-secrets
                  key: jwt-secret
          resources:
            requests:
              cpu: 500m
              memory: 512Mi
            limits:
              cpu: 1000m
              memory: 1Gi
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 5
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: exchange-gateway-hpa
  namespace: turbox-exchange
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: exchange-gateway
  minReplicas: 3
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

**撮合引擎 (StatefulSet)**

每个交易对独立部署，Active + Standby 热备。

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: exchange-match-btcusdt
  namespace: turbox-exchange
spec:
  serviceName: exchange-match-btcusdt
  replicas: 2  # Active + Standby
  selector:
    matchLabels:
      app: exchange-match
      symbol: btcusdt
  template:
    metadata:
      labels:
        app: exchange-match
        symbol: btcusdt
    spec:
      containers:
        - name: match
          image: turbox/exchange-match:latest
          env:
            - name: MATCH_SYMBOL
              value: "BTCUSDT"
          resources:
            requests:
              cpu: 1000m
              memory: 1Gi
            limits:
              cpu: 2000m
              memory: 2Gi
          volumeMounts:
            - name: snapshot-storage
              mountPath: /data/snapshots
  volumeClaimTemplates:
    - metadata:
        name: snapshot-storage
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 10Gi
```

### 5.3 Ingress 配置

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: turbox-ingress
  namespace: turbox-exchange
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "3600"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "3600"
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
spec:
  tls:
    - hosts:
        - api.turbox.io
        - ws.turbox.io
      secretName: turbox-tls
  rules:
    - host: api.turbox.io
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: exchange-gateway
                port:
                  number: 8080
    - host: ws.turbox.io
      http:
        paths:
          - path: /ws
            pathType: Prefix
            backend:
              service:
                name: exchange-market
                port:
                  number: 8085
```

### 5.4 ConfigMap / Secret 管理

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: turbox-config
  namespace: turbox-exchange
data:
  nacos-server-addr: "nacos.turbox-infra.svc:8848"
  kafka-bootstrap-servers: "kafka.turbox-infra.svc:9092"
  redis-host: "redis.turbox-infra.svc"
  db-host: "mysql.turbox-infra.svc"
---
apiVersion: v1
kind: Secret
metadata:
  name: turbox-secrets
  namespace: turbox-exchange
type: Opaque
data:
  db-password: <base64-encoded>
  redis-password: <base64-encoded>
  jwt-secret: <base64-encoded>
  minio-access-key: <base64-encoded>
  minio-secret-key: <base64-encoded>
```

### 5.5 健康检查

所有服务通过 Spring Boot Actuator 暴露健康端点：

| 端点 | 用途 | K8s Probe |
| ---- | ---- | --------- |
| `/actuator/health/liveness` | 进程是否存活 | livenessProbe |
| `/actuator/health/readiness` | 是否可接收流量 | readinessProbe |
| `/actuator/prometheus` | Prometheus 指标 | - |

---

## 6. 数据库初始化

### 6.1 建表 SQL

建表 SQL 文件位于 `docker/init-sql/` 目录下，按服务划分：

```
docker/init-sql/
├── 01-user.sql           # user 表
├── 02-account.sql        # account, account_flow 表
├── 03-order.sql          # orders, trades 表
├── 04-market.sql         # kline 表
├── 05-config.sql         # symbol_config, currency_config 表
└── 06-outbox.sql         # outbox_event 表
```

MySQL 容器启动时自动执行 `/docker-entrypoint-initdb.d/` 下的 SQL 文件。

### 6.2 初始数据

**交易对配置**

```sql
INSERT INTO symbol_config (id, symbol, base_currency, quote_currency,
  price_precision, quantity_precision, min_quantity, max_quantity,
  min_notional, tick_size, step_size, maker_fee_rate, taker_fee_rate,
  status, sort_order, created_at, updated_at)
VALUES
  (1, 'BTCUSDT', 'BTC', 'USDT', 2, 6, 0.000001, 100, 10, 0.01, 0.000001, 0.001, 0.002, 1, 1, NOW(), NOW()),
  (2, 'ETHUSDT', 'ETH', 'USDT', 2, 4, 0.0001, 5000, 10, 0.01, 0.0001, 0.001, 0.002, 1, 2, NOW(), NOW());
```

**币种配置**

```sql
INSERT INTO currency_config (id, currency, chain, confirmations,
  min_deposit, min_withdrawal, withdrawal_fee, audit_threshold,
  deposit_enabled, withdrawal_enabled, created_at, updated_at)
VALUES
  (1, 'BTC', 'BTC', 3, 0.0001, 0.001, 0.0005, 1, 1, 1, NOW(), NOW()),
  (2, 'ETH', 'ERC20', 12, 0.01, 0.01, 0.005, 10, 1, 1, NOW(), NOW()),
  (3, 'USDT', 'ERC20', 12, 1, 10, 5, 50000, 1, 1, NOW(), NOW()),
  (4, 'USDT', 'TRC20', 20, 1, 10, 1, 50000, 1, 1, NOW(), NOW());
```

### 6.3 ShardingSphere 分片配置

在 `application.yml` 中配置分片规则（以 orders 表为例）：

```yaml
spring:
  shardingsphere:
    datasource:
      names: ds0
      ds0:
        type: com.zaxxer.hikari.HikariDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        jdbc-url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
        username: ${DB_USER}
        password: ${DB_PASS}
    rules:
      sharding:
        tables:
          orders:
            actual-data-nodes: ds0.orders_$->{0..15}
            table-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: orders-inline
          account_flow:
            actual-data-nodes: ds0.account_flow_$->{0..15}
            table-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: account-flow-inline
          trades:
            actual-data-nodes: ds0.trades_$->{0..7}
            table-strategy:
              standard:
                sharding-column: symbol
                sharding-algorithm-name: trades-hash
        sharding-algorithms:
          orders-inline:
            type: INLINE
            props:
              algorithm-expression: orders_$->{user_id % 16}
          account-flow-inline:
            type: INLINE
            props:
              algorithm-expression: account_flow_$->{user_id % 16}
          trades-hash:
            type: HASH_MOD
            props:
              sharding-count: 8
```

---

## 7. 监控告警

### 7.1 Prometheus 配置

各服务通过 Spring Boot Actuator 暴露 `/actuator/prometheus` 端点。

Prometheus 抓取配置：

```yaml
scrape_configs:
  - job_name: 'turbox-services'
    metrics_path: '/actuator/prometheus'
    nacos_sd_configs:
      - server: 'nacos:8848'
    # 或使用 kubernetes_sd_configs（K8s 环境）
    static_configs:
      - targets:
          - 'exchange-gateway:8080'
          - 'exchange-user:8081'
          - 'exchange-account:8082'
          - 'exchange-order:8083'
          - 'exchange-match:8084'
          - 'exchange-market:8085'
          - 'exchange-risk:8086'
          - 'exchange-admin:8087'
```

### 7.2 Grafana Dashboard

推荐导入以下 Dashboard：

| Dashboard | ID | 用途 |
| --------- | -- | ---- |
| Spring Boot Statistics | 12900 | 服务整体指标 |
| JVM (Micrometer) | 4701 | JVM 详细指标 |
| MySQL Overview | 7362 | 数据库监控 |
| Redis Dashboard | 763 | Redis 监控 |
| Kafka Overview | 7589 | Kafka 监控 |

自定义业务 Dashboard 关注：

- 撮合引擎：队列深度、撮合延迟 P99/P999、吞吐量
- 订单服务：下单 QPS、成功率、全链路延迟
- 账户服务：结算 TPS、余额异常告警
- 行情服务：WebSocket 连接数、推送延迟

### 7.3 告警规则

| 告警 | 条件 | 级别 | 通知方式 |
| ---- | ---- | ---- | -------- |
| 服务不可用 | 健康检查失败 > 30s | P0 | 电话 + 钉钉 |
| 高错误率 | 错误率 > 5% 持续 1 分钟 | P1 | 钉钉 + 邮件 |
| 高延迟 | P99 > 1s 持续 5 分钟 | P1 | 钉钉 + 邮件 |
| 撮合积压 | 队列深度 > 10,000 | P1 | 钉钉 + 邮件 |
| 磁盘空间不足 | 使用率 > 80% | P2 | 邮件 |
| MySQL 慢查询 | 慢查询数 > 100/分钟 | P2 | 邮件 |
| Kafka 消费延迟 | Lag > 10,000 | P1 | 钉钉 + 邮件 |
| DLQ 有消息 | DLQ 消息数 > 0 | P1 | 钉钉 + 邮件 |

Prometheus 告警规则示例：

```yaml
groups:
  - name: turbox-alerts
    rules:
      - alert: ServiceDown
        expr: up == 0
        for: 30s
        labels:
          severity: P0
        annotations:
          summary: "服务 {{ $labels.job }} 不可用"

      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[1m]) / rate(http_server_requests_seconds_count[1m]) > 0.05
        for: 1m
        labels:
          severity: P1
        annotations:
          summary: "服务 {{ $labels.job }} 错误率超过 5%"

      - alert: HighLatency
        expr: histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: P1
        annotations:
          summary: "服务 {{ $labels.job }} P99 延迟超过 1 秒"

      - alert: MatchEngineBacklog
        expr: match_engine_queue_size > 10000
        for: 1m
        labels:
          severity: P1
        annotations:
          summary: "撮合引擎队列积压: {{ $value }}"
```
