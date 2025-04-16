FROM eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
EXPOSE 8081
COPY target/auth-1.0-SNAPSHOT-exec.jar auth.jar

CMD ["java", "-jar", "auth.jar"]