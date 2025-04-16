FROM eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
EXPOSE 8083
COPY target/bank-1.0-SNAPSHOT-exec.jar bank.jar

CMD ["java", "-jar", "bank.jar"]