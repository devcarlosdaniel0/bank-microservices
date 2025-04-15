FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8082
COPY target/mail-0.0.1-SNAPSHOT.jar mail.jar

CMD ["java", "-jar", "mail.jar"]