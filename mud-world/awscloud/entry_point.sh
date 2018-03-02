#!/bin/bash
set -o errexit
set -o xtrace

if [[ "${DEPLOY_ENV:-default}" = "AWS" ]]; then
	export EC2_HOST=$(wget -O - http://169.254.169.254/latest/meta-data/local-ipv4 2> /dev/null)
	result="$(python ecs-get-port-mapping.py)"
	eval "$result"
fi

java -jar -Djava.security.egd=file:/dev/./urandom app.jar