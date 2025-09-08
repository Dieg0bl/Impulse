# IMPULSE - Makefile for development

.PHONY: help install build test clean run-backend run-frontend run-all stop docker-up docker-down

# Default target
help:
	@echo "IMPULSE - Human Validation Platform"
	@echo ""
	@echo "Available commands:"
	@echo "  install      - Install all dependencies"
	@echo "  build        - Build backend and frontend"
	@echo "  test         - Run all tests"
	@echo "  clean        - Clean build artifacts"
	@echo "  run-backend  - Run backend server"
	@echo "  run-frontend - Run frontend dev server"
	@echo "  run-all      - Run both backend and frontend"
	@echo "  stop         - Stop all running services"
	@echo "  docker-up    - Start with Docker Compose"
	@echo "  docker-down  - Stop Docker Compose"

# Install dependencies
install:
	@echo "Installing backend dependencies..."
	cd backend && mvn clean install -DskipTests
	@echo "Installing frontend dependencies..."
	cd frontend && npm install
	@echo "✅ All dependencies installed"

# Build all
build:
	@echo "Building backend..."
	cd backend && mvn clean package -DskipTests
	@echo "Building frontend..."
	cd frontend && npm run build
	@echo "✅ Build completed"

# Run tests
test:
	@echo "Running backend tests..."
	cd backend && mvn test
	@echo "Running frontend tests..."
	cd frontend && npm test
	@echo "✅ All tests completed"

# Clean artifacts
clean:
	@echo "Cleaning backend..."
	cd backend && mvn clean
	@echo "Cleaning frontend..."
	cd frontend && rm -rf dist node_modules/.cache
	@echo "✅ Clean completed"

# Run backend
run-backend:
	@echo "Starting backend server..."
	cd backend && mvn spring-boot:run

# Run frontend
run-frontend:
	@echo "Starting frontend dev server..."
	cd frontend && npm run dev

# Run both (requires separate terminals)
run-all:
	@echo "To run both services:"
	@echo "Terminal 1: make run-backend"
	@echo "Terminal 2: make run-frontend"
	@echo ""
	@echo "Or use Docker: make docker-up"

# Stop services (manual stop with Ctrl+C)
stop:
	@echo "Stop services with Ctrl+C in their respective terminals"
	@echo "Or use: make docker-down"

# Docker commands (if docker-compose is added later)
docker-up:
	@echo "Docker Compose not configured yet"
	@echo "Use: make run-backend and make run-frontend"

docker-down:
	@echo "Docker Compose not configured yet"

# Database setup
db-setup:
	@echo "Setting up database..."
	@echo "Make sure MySQL is running, then:"
	@echo "mysql -u root -p < backend/db/schema/impulse_lean.sql"

# Full setup for new environment
setup: install db-setup
	@echo "✅ Full setup completed"
	@echo "Now run: make run-backend (in one terminal)"
	@echo "And run: make run-frontend (in another terminal)"
