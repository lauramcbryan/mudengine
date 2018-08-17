FROM postgres:alpine

COPY ./scripts/01-*.sql /docker-entrypoint-initdb.d/
COPY ./scripts/02-*.sql /docker-entrypoint-initdb.d/
COPY ./scripts/03-*.sql /docker-entrypoint-initdb.d/

