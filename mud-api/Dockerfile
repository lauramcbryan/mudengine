FROM openjdk:8-jre-alpine

VOLUME /tmp

RUN apk --no-cache add bash


EXPOSE 8080

ADD infra/entry_point.shell entry_point.sh
ADD target/mud-api-*.jar app.jar

RUN ["chmod", "+x", "entry_point.sh"]
ENTRYPOINT ["/bin/bash", "entry_point.sh"]
