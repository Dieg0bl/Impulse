-- Script de configuración de base de datos MySQL para IMPULSE
-- Cumple compliance: RGPD, ISO 27001, ENS
-- Charset: utf8mb4_unicode_ci para soporte completo Unicode

-- Crear base de datos impulse si no existe
CREATE DATABASE IF NOT EXISTS impulse 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE impulse;

-- Crear usuario específico para la aplicación (siguiendo principio de menor privilegio)
CREATE USER IF NOT EXISTS 'impulse_app'@'localhost' IDENTIFIED BY 'impulse_secure_2025!';

-- Otorgar permisos específicos solo para la base de datos impulse
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON impulse.* TO 'impulse_app'@'localhost';

-- Refrescar privilegios
FLUSH PRIVILEGES;

-- Mostrar información de la base de datos
SHOW DATABASES;
SELECT 'Base de datos IMPULSE configurada correctamente' as Status;
