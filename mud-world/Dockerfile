FROM rappdw/docker-java-python

VOLUME /tmp

ADD awscloud/ecs-get-port-mapping.py ecs-get-port-mapping.py
ADD awscloud/entry_point.sh entry_point.sh
ADD target/mud-world-*.jar app.jar

RUN ["chmod", "+x", "entry_point.sh"]
ENTRYPOINT ["/bin/bash", "/entry_point.sh"]

