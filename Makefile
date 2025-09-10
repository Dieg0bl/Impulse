# IMPULSE - Makefile para desarrollo y deployment
.PHONY: help init up down build test clean db-reset db-migrate logs dev-backend dev-frontend deploy

# Variables
DOCKER_COMPOSE = docker-compose -f infra/docker-compose.yml
ENV_FILE = .env
BACKEND_DIR = backend
FRONTEND_DIR = frontend

# Colores para output
GREEN = \033[0;32m
YELLOW = \033[1;33m
RED = \033[0;31m
NC = \033[0m # No Color

help: ## Muestra esta ayuda
	@echo "$(GREEN)IMPULSE - Comandos disponibles:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Ejemplos de uso:$(NC)"
	@echo "  make init          # Primera vez"
	@echo "  make up            # Levantar servicios"
	@echo "  make dev           # Desarrollo local"
	@echo "  make test          # Ejecutar tests"

init: ## Inicialización completa del proyecto
	@echo "$(GREEN)🚀 Inicializando IMPULSE...$(NC)"
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "$(YELLOW)📄 Copiando .env.template a .env...$(NC)"; \
		cp .env.template $(ENV_FILE); \
	fi
	@echo "$(YELLOW)🔧 Creando directorios necesarios...$(NC)"
	@mkdir -p infra/mysql/data
	@mkdir -p infra/redis/data
	@mkdir -p infra/minio/data
	@mkdir -p backend/keys
	@mkdir -p logs
	@echo "$(YELLOW)🔑 Generando claves RSA para JWT...$(NC)"
	@$(MAKE) generate-jwt-keys
	@echo "$(YELLOW)🐳 Construyendo imágenes Docker...$(NC)"
	@$(DOCKER_COMPOSE) build
	@echo "$(GREEN)✅ Inicialización completada!$(NC)"
	@echo "$(YELLOW)⚠️  Recuerda editar el archivo .env con tus configuraciones$(NC)"

up: ## Levantar todos los servicios en Docker
	@echo "$(GREEN)🐳 Levantando servicios IMPULSE...$(NC)"
	@$(DOCKER_COMPOSE) --env-file $(ENV_FILE) up -d
	@echo "$(GREEN)✅ Servicios levantados!$(NC)"
	@echo "$(YELLOW)📍 URLs disponibles:$(NC)"
	@echo "  Backend:    http://localhost:8080"
	@echo "  Frontend:   http://localhost:3000"
	@echo "  MinIO:      http://localhost:9001"
	@echo "  Mailhog:    http://localhost:8025"
	@echo "  MySQL:      localhost:3306"
	@echo "  Redis:      localhost:6379"

down: ## Parar todos los servicios
	@echo "$(YELLOW)🛑 Parando servicios IMPULSE...$(NC)"
	@$(DOCKER_COMPOSE) down
	@echo "$(GREEN)✅ Servicios parados$(NC)"

build: ## Construir imágenes Docker
	@echo "$(YELLOW)🔨 Construyendo imágenes...$(NC)"
	@$(DOCKER_COMPOSE) build --no-cache
	@echo "$(GREEN)✅ Imágenes construidas$(NC)"

test: ## Ejecutar todos los tests
	@echo "$(GREEN)🧪 Ejecutando tests...$(NC)"
	@cd $(BACKEND_DIR) && mvn test
	@if [ -d $(FRONTEND_DIR) ]; then \
		echo "$(YELLOW)🧪 Ejecutando tests frontend...$(NC)"; \
		cd $(FRONTEND_DIR) && npm test; \
	fi
	@echo "$(GREEN)✅ Tests completados$(NC)"

test-backend: ## Ejecutar solo tests del backend
	@echo "$(YELLOW)🧪 Ejecutando tests backend...$(NC)"
	@cd $(BACKEND_DIR) && mvn test
	@echo "$(GREEN)✅ Tests backend completados$(NC)"

clean: ## Limpiar contenedores, imágenes y volúmenes
	@echo "$(RED)🧹 Limpiando Docker...$(NC)"
	@$(DOCKER_COMPOSE) down -v --remove-orphans
	@docker system prune -f
	@echo "$(GREEN)✅ Limpieza completada$(NC)"

db-reset: ## Resetear base de datos (CUIDADO: borra todos los datos)
	@echo "$(RED)⚠️  ADVERTENCIA: Esto borrará TODOS los datos de la BD$(NC)"
	@read -p "¿Estás seguro? (yes/no): " confirm && [ "$$confirm" = "yes" ]
	@$(DOCKER_COMPOSE) stop mysql
	@docker volume rm impulse_mysql_data 2>/dev/null || true
	@$(DOCKER_COMPOSE) up -d mysql
	@echo "$(GREEN)✅ Base de datos reseteada$(NC)"

db-migrate: ## Ejecutar migraciones de base de datos
	@echo "$(YELLOW)📊 Ejecutando migraciones...$(NC)"
	@cd $(BACKEND_DIR) && mvn flyway:migrate
	@echo "$(GREEN)✅ Migraciones ejecutadas$(NC)"

db-seed: ## Cargar datos de prueba
	@echo "$(YELLOW)🌱 Cargando datos de prueba...$(NC)"
	@cd $(BACKEND_DIR) && mvn exec:java -Dexec.mainClass="com.impulse.lean.SeedDataApplication"
	@echo "$(GREEN)✅ Datos de prueba cargados$(NC)"

logs: ## Ver logs de todos los servicios
	@$(DOCKER_COMPOSE) logs -f

logs-backend: ## Ver logs solo del backend
	@$(DOCKER_COMPOSE) logs -f backend

logs-mysql: ## Ver logs solo de MySQL
	@$(DOCKER_COMPOSE) logs -f mysql

dev: ## Desarrollo local (backend + frontend)
	@echo "$(GREEN)🚀 Iniciando desarrollo local...$(NC)"
	@$(MAKE) up
	@echo "$(YELLOW)⚡ Servicios Docker levantados$(NC)"
	@echo "$(YELLOW)🔧 Para desarrollo, ejecuta en terminales separadas:$(NC)"
	@echo "  make dev-backend"
	@echo "  make dev-frontend"

dev-backend: ## Desarrollo backend local (fuera de Docker)
	@echo "$(YELLOW)⚡ Iniciando backend en modo desarrollo...$(NC)"
	@cd $(BACKEND_DIR) && mvn spring-boot:run -Dspring.profiles.active=dev

dev-frontend: ## Desarrollo frontend local
	@echo "$(YELLOW)⚡ Iniciando frontend en modo desarrollo...$(NC)"
	@if [ -d $(FRONTEND_DIR) ]; then \
		cd $(FRONTEND_DIR) && npm run dev; \
	else \
		echo "$(RED)❌ Directorio frontend no encontrado$(NC)"; \
	fi

generate-jwt-keys: ## Generar claves RSA para JWT
	@echo "$(YELLOW)🔑 Generando claves RSA...$(NC)"
	@mkdir -p $(BACKEND_DIR)/keys
	@if [ ! -f $(BACKEND_DIR)/keys/private_key.pem ]; then \
		openssl genrsa -out $(BACKEND_DIR)/keys/private_key.pem 2048; \
		openssl rsa -in $(BACKEND_DIR)/keys/private_key.pem -pubout -out $(BACKEND_DIR)/keys/public_key.pem; \
		echo "$(GREEN)✅ Claves RSA generadas$(NC)"; \
	else \
		echo "$(YELLOW)⚠️  Las claves RSA ya existen$(NC)"; \
	fi

security-check: ## Verificar configuración de seguridad
	@echo "$(GREEN)🔒 Verificando configuración de seguridad...$(NC)"
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "$(RED)❌ Archivo .env no encontrado$(NC)"; \
		exit 1; \
	fi
	@if grep -q "change_in_production" $(ENV_FILE); then \
		echo "$(RED)⚠️  ADVERTENCIA: Contraseñas por defecto detectadas$(NC)"; \
	fi
	@if [ -f $(BACKEND_DIR)/keys/private_key.pem ]; then \
		echo "$(GREEN)✅ Claves RSA encontradas$(NC)"; \
	else \
		echo "$(RED)❌ Claves RSA no encontradas$(NC)"; \
	fi

health-check: ## Verificar estado de servicios
	@echo "$(GREEN)💚 Verificando estado de servicios...$(NC)"
	@curl -f http://localhost:8080/actuator/health 2>/dev/null && echo "$(GREEN)✅ Backend OK$(NC)" || echo "$(RED)❌ Backend DOWN$(NC)"
	@curl -f http://localhost:9001/minio/health/live 2>/dev/null && echo "$(GREEN)✅ MinIO OK$(NC)" || echo "$(RED)❌ MinIO DOWN$(NC)"
	@redis-cli -h localhost -p 6379 ping 2>/dev/null && echo "$(GREEN)✅ Redis OK$(NC)" || echo "$(RED)❌ Redis DOWN$(NC)"

backup: ## Crear backup de la base de datos
	@echo "$(YELLOW)💾 Creando backup...$(NC)"
	@mkdir -p backups
	@docker exec impulse-mysql mysqldump -u root -p$(shell grep MYSQL_ROOT_PASSWORD $(ENV_FILE) | cut -d'=' -f2) impulse_db > backups/backup_$(shell date +%Y%m%d_%H%M%S).sql
	@echo "$(GREEN)✅ Backup creado en backups/$(NC)"

restore: ## Restaurar backup de la base de datos
	@echo "$(YELLOW)📥 Restaurando backup...$(NC)"
	@ls -la backups/
	@read -p "Archivo a restaurar: " file && \
	docker exec -i impulse-mysql mysql -u root -p$(shell grep MYSQL_ROOT_PASSWORD $(ENV_FILE) | cut -d'=' -f2) impulse_db < backups/$$file
	@echo "$(GREEN)✅ Backup restaurado$(NC)"

deploy-staging: ## Deploy a staging
	@echo "$(YELLOW)🚀 Desplegando a staging...$(NC)"
	@git push staging main
	@echo "$(GREEN)✅ Deploy a staging completado$(NC)"

deploy-prod: ## Deploy a producción (CUIDADO)
	@echo "$(RED)⚠️  DEPLOY A PRODUCCIÓN$(NC)"
	@read -p "¿Estás seguro? (yes/no): " confirm && [ "$$confirm" = "yes" ]
	@git push production main
	@echo "$(GREEN)✅ Deploy a producción completado$(NC)"

install-deps: ## Instalar dependencias
	@echo "$(YELLOW)📦 Instalando dependencias...$(NC)"
	@if [ -d $(FRONTEND_DIR) ]; then \
		cd $(FRONTEND_DIR) && npm install; \
	fi
	@echo "$(GREEN)✅ Dependencias instaladas$(NC)"

format: ## Formatear código
	@echo "$(YELLOW)🎨 Formateando código...$(NC)"
	@cd $(BACKEND_DIR) && mvn spotless:apply
	@if [ -d $(FRONTEND_DIR) ]; then \
		cd $(FRONTEND_DIR) && npm run format; \
	fi
	@echo "$(GREEN)✅ Código formateado$(NC)"

lint: ## Ejecutar linters
	@echo "$(YELLOW)🔍 Ejecutando linters...$(NC)"
	@cd $(BACKEND_DIR) && mvn spotless:check
	@if [ -d $(FRONTEND_DIR) ]; then \
		cd $(FRONTEND_DIR) && npm run lint; \
	fi
	@echo "$(GREEN)✅ Linters completados$(NC)"

# Comandos de información
info: ## Mostrar información del proyecto
	@echo "$(GREEN)ℹ️  IMPULSE - Información del proyecto$(NC)"
	@echo "  Versión:     v1.0.0"
	@echo "  Backend:     Spring Boot 3.3.3 + Java 17"
	@echo "  Frontend:    React + TypeScript + PWA"
	@echo "  Base datos:  MySQL 8"
	@echo "  Cache:       Redis 7"
	@echo "  Storage:     MinIO (S3-compatible)"
	@echo "  Email:       Mailhog (dev) / Brevo (prod)"
	@echo ""
	@echo "$(YELLOW)📁 Estructura:$(NC)"
	@tree -L 2 . 2>/dev/null || ls -la

# Comandos de utilidad
prune: ## Limpiar Docker completo (CUIDADO)
	@echo "$(RED)🧹 Limpieza completa de Docker...$(NC)"
	@read -p "¿Estás seguro? (yes/no): " confirm && [ "$$confirm" = "yes" ]
	@docker system prune -a -f --volumes
	@echo "$(GREEN)✅ Limpieza completa realizada$(NC)"

update: ## Actualizar dependencias
	@echo "$(YELLOW)📦 Actualizando dependencias...$(NC)"
	@cd $(BACKEND_DIR) && mvn versions:use-latest-versions
	@if [ -d $(FRONTEND_DIR) ]; then \
		cd $(FRONTEND_DIR) && npm update; \
	fi
	@echo "$(GREEN)✅ Dependencias actualizadas$(NC)">
