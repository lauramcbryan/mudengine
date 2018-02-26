#!/bin/sh
set -o errexit
set -o xtrace

export EC2_HOST=$(wget -O - http://169.254.169.254/latest/meta-data/local-ipv4 2> /dev/null)
result="$(python /opt/bin/ecs-get-port-mapping.py)"
eval "$result"

java -jar -Djava.security.egd=file:/dev/./urandom app.jar