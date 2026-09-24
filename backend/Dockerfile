FROM eclipse-temurin:21-jre-alpine
RUN mkdir /opt/app
COPY ./target/*.jar /opt/app/app.jar
WORKDIR /opt/app
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "/opt/app/app.jar"]
