# backend/Dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/wait-for-it.sh /app/wait-for-it.sh
EXPOSE 8080

# Add curl and netcat for healthcheck and wait-for-it script
RUN apt-get update && apt-get install -y curl netcat-openbsd && rm -rf /var/lib/apt/lists/* \
    && chmod +x /app/wait-for-it.sh

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Add more memory for Java and improve stability
# Use wait-for-it script to wait for MySQL before starting the application
ENTRYPOINT ["/app/wait-for-it.sh", "mysql", "3306", "sh", "-c", "java ${JAVA_OPTS:--Xmx512m -Xms256m} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
