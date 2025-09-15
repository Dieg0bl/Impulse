# Gestión de Entornos y Bases de Datos en Impulse

Impulse sigue las mejores prácticas de desarrollo profesional, utilizando tres entornos separados:

## 1. Producción (prod)
- Base de datos real, segura y estable.
- Configuración en `application-prod.properties`.
- Ejemplo: MySQL, credenciales reales, acceso restringido.

## 2. Desarrollo (dev)
- Base de datos para desarrollo local.
- Configuración en `application-dev.properties`.
- Ejemplo: MySQL, datos ficticios, acceso para desarrolladores.

## 3. Pruebas (test)
- Base de datos desechable para pruebas automatizadas.
- Configuración en `application-test.properties`.
- Ejemplo: H2 en memoria, datos generados por tests.

## Selección de perfil en Spring Boot
Spring Boot permite seleccionar el entorno usando el parámetro:

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=test
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Seguridad
Nunca uses credenciales reales en archivos públicos. Utiliza variables de entorno o servicios de gestión de secretos en producción.

---

**Esta estructura garantiza calidad, seguridad y facilidad de pruebas en el ciclo de desarrollo.**
