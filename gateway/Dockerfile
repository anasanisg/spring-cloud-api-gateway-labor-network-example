FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/gateway-1.0.0.jar /app/app.jar

EXPOSE 8080

CMD [ "java","-jar","app.jar"]