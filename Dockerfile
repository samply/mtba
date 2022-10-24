FROM amazoncorretto:15-alpine

COPY target/mtba.jar /app/

WORKDIR /app

CMD ["java", "-jar", "mtba.jar"]
