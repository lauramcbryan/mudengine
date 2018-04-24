build:
	docker-compose -f ../docker-compose.yml build
start-stack:
	docker-compose -f ../docker-compose.yml up --build -d
stop-stack:
	docker-compose -f ../docker-compose.yml down --remove-orphans
start-app:
	docker-compose -f ../docker-compose.yml up --build -d  ${APP}
	