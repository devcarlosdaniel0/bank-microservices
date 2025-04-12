FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080
COPY target/gateway-0.0.1-SNAPSHOT.jar gateway.jar

CMD ["java", "-jar", "gateway.jar"]