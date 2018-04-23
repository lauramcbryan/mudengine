FROM openjdk:8-jre-alpine

VOLUME /tmp

EXPOSE 8761

ADD target/mud-registry-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
