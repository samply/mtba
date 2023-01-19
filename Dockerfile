FROM eclipse-temurin:17-jre-alpine

COPY target/mtba.jar /app/

RUN apk update && apk add python3

WORKDIR /app
RUN mkdir /app/temp-files
RUN chmod 777 /app/temp-files
ENV TEMPORAL_DIRECTORY /app/temp-files

CMD ["java", "-jar", "mtba.jar"]
