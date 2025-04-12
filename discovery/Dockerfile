FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8761
COPY target/discovery-0.0.1-SNAPSHOT.jar discovery.jar

CMD ["java", "-jar", "discovery.jar"]