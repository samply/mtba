FROM amazoncorretto:17.0.5-alpine

COPY target/mtba.jar /app/

RUN yum install python3

WORKDIR /app

CMD ["java", "-jar", "mtba.jar"]
