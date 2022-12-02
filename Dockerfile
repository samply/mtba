FROM eclipse-temurin:17-jre-alpine

COPY target/mtba.jar /app/

RUN apk update && apk add python3

WORKDIR /app

CMD ["java", "-jar", "mtba.jar"]
