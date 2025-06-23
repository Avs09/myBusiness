
  up:
  	docker-compose up -d --build

  down:
  	docker-compose down

  ps:
  	docker-compose ps

  logs-backend:
  	docker logs -f mybusiness-backend

  logs-db:
  	docker logs -f mybusiness-db

  logs-frontend:
  	docker logs -f mybusiness-frontend
 