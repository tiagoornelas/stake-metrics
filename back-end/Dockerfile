FROM gradle:7.6-jdk-alpine

WORKDIR /app
COPY . /app

USER root
RUN chown -R gradle /app
RUN rm -rf /home/gradle/.gradle/jdks

USER gradle
RUN gradle bootJar

FROM openjdk:17-jdk-alpine
COPY --from=0 /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx8g","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-jar","/app.jar"]
