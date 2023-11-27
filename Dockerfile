FROM eclipse-temurin:17-jre

COPY target/mtba.jar /app/

RUN apt-get update \
    && apt-get install -y python3 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*


WORKDIR /app
RUN mkdir /app/temp-files
RUN chmod 777 /app/temp-files
ENV TEMPORAL_DIRECTORY /app/temp-files
ENV MUTATIONS_CSV_SCRIPT_INTERPRETER "python3"
ENV NEW_FILES_DIRECTORY "/app/input"
ENV PERSIST_DIRECTORY "/app/persist"

CMD ["java", "-jar", "mtba.jar"]
