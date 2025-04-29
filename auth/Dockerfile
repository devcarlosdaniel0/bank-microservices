FROM eclipse-temurin:17-jre-alpine-3.21

WORKDIR /app
EXPOSE 8081
EXPOSE 5006

COPY target/auth-1.0-SNAPSHOT-exec.jar auth.jar

SHELL ["/bin/sh", "-c"]
CMD java ${AUTH_ADDITIONAL_OPTS} -jar auth.jar