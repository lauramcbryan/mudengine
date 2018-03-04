FROM openjdk:8-jre-alpine

RUN apk --no-cache add python
RUN apk --no-cache add py-pip
RUN apk --no-cache add bash
RUN pip install boto
RUN pip install boto3
RUN pip install requests
RUN pip install datetime

VOLUME /tmp

ADD awscloud/ecs-get-port-mapping.py ecs-get-port-mapping.py
ADD awscloud/entry_point.sh entry_point.sh
ADD target/mud-being-*.jar app.jar

RUN ["chmod", "+x", "entry_point.sh"]
ENTRYPOINT ["/bin/bash", "entry_point.sh"]

