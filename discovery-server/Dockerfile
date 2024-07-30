FROM amazoncorretto:17-alpine

WORKDIR /app

COPY target/discovery-server-1.0.0.jar /app/app.jar

EXPOSE 8761

CMD [ "java","-jar","app.jar"]