FROM eclipse-temurin:17-jre-alpine

COPY target/mtba.jar /app/

RUN apk update && apk upgrade && apk add python3

WORKDIR /app
RUN mkdir /app/temp-files
RUN chmod 777 /app/temp-files
ENV TEMPORAL_DIRECTORY /app/temp-files
ENV MUTATIONS_CSV_SCRIPT_INTERPRETER "python3"
ENV NEW_FILES_DIRECTORY "/app/input"
ENV PERSIST_DIRECTORY "/app/persist"

CMD ["java", "-jar", "mtba.jar"]
