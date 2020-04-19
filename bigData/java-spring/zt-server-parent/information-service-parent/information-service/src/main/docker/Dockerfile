FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD information-service.jar information-service.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/information-service.jar"]