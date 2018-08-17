FROM vault:latest

RUN mkdir vault-keys
WORKDIR vault-keys

COPY ./vault-initialize.shell vault-initialize.sh
RUN chmod +x vault-initialize.sh
