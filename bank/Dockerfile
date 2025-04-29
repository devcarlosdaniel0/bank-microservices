FROM eclipse-temurin:17-jre-alpine-3.21

WORKDIR /app
EXPOSE 8083
EXPOSE 5005

COPY target/bank-1.0-SNAPSHOT-exec.jar bank.jar

SHELL ["/bin/sh", "-c"]
CMD java ${BANK_ADDITIONAL_OPTS} -jar bank.jar