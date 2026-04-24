.PHONY: build test up down

build:
	mvn -q -DskipTests clean package

test:
	mvn -q test

up:
	docker compose -f docker/compose/docker-compose.yml up -d --build

down:
	docker compose -f docker/compose/docker-compose.yml down -v
