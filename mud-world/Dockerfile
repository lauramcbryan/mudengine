FROM openjdk:8-jre-alpine

VOLUME /tmp
ARG JAR_FILE
ADD target/mud-world-*.jar app.jar
ENTRYPOINT ["java","-Xmx256m", "-Xss32m", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

