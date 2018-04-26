FROM openjdk:8-jre-alpine

RUN apk --no-cache add bash

VOLUME /tmp

ADD awscloud/entry_point.shell entry_point.sh
ADD target/mud-config-*.jar app.jar

RUN ["chmod", "+x", "entry_point.sh"]
ENTRYPOINT ["/bin/bash", "entry_point.sh"]
