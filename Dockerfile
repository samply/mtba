FROM amazoncorretto:17-al2022-RC

COPY target/mtba.jar /app/

RUN yum install python3

WORKDIR /app

CMD ["java", "-jar", "mtba.jar"]
