# 使用多阶段构建
# 阶段1: 构建
FROM maven:3.9.eclipse-temurin-17 AS builder

WORKDIR /build

# 先复制 pom.xml, 利用 Docker 缓存
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY src ./src
RUN mvn package -DskipTests -B

# 阶段2: 运行
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UserG1GC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
