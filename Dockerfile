FROM eclipse-temurin:17-jre-alpine

COPY target/mtba.jar /app/

RUN apk update && apk add python3

WORKDIR /app
RUN mkdir /app/mtba-files
RUN mkdir /app/mtba-files/input
RUN mkdir /app/mtba-files/temp
RUN mkdir /app/mtba-files/persist
ENV NEW_FILES_DIRECTORY /app/mtba-files/input
ENV TEMPORAL_DIRECTORY /app/mtba-files/temp
ENV PERSIST_DIRECTORY /app/mtba-files/persist

CMD ["java", "-jar", "mtba.jar"]
