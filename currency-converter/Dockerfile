FROM eclipse-temurin:17-jre-alpine-3.21
WORKDIR /app
EXPOSE 8084
COPY target/currency-converter-0.0.1-SNAPSHOT.jar currency.jar

CMD ["java", "-jar", "currency.jar"]