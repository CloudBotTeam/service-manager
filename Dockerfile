FROM openjdk:8-jdk-alpine
MAINTAINER mwish

COPY Service-Manager/target/*.jar /app.jar

EXPOSE 8101

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]