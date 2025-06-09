ROOT_DIR=..
DOCKER_DIR=./docker

build:
	./mvnw clean package -DskipTests

#up:
	#docker compose -f compose.yaml up -d --no-deps --build backend
up:
	docker compose -f compose.yaml up -d --build backend

deploy: build up
