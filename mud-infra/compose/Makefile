build: package
	docker-compose docker-compose.yml build
start-stack: package
	docker-compose docker-compose.yml up --build -d
stop-stack:
	docker-compose docker-compose.yml down --remove-orphans
package:
	mvn clean
	mvn $(ARGS) package
rebuild-container:
	docker-compose docker-compose.yml stop -t 1 $(APP)
	mvn clean
	mvn package
rebuild-app: rebuild-container
	docker-compose docker-compose.yml build $(APP)
	docker-compose docker-compose.yml create $(APP)
	docker-compose docker-compose.yml start $(APP)
	docker-compose docker-compose.yml logs -f $(APP)
