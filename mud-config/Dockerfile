FROM openjdk:8-jre-alpine

VOLUME /tmp

EXPOSE 8888

ADD target/mud-config-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
