FROM openjdk:8-jre-alpine

VOLUME /tmp

ADD infra/entry_point.shell entry_point.sh
ADD target/mud-action-*.jar app.jar

RUN ["chmod", "+x", "entry_point.sh"]
ENTRYPOINT ["/bin/sh", "entry_point.sh"]

