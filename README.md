# Impulse

## Propósito del Proyecto

Impulse es una plataforma de accountability social diseñada para promover la responsabilidad y transparencia en comunidades y organizaciones. Esta aplicación permite crear, gestionar y hacer seguimiento de compromisos públicos, fomentando la cultura de cumplimiento y responsabilidad social.

### Características Principales
- Gestión de compromisos y metas públicas
- Sistema de seguimiento y reportes de progreso
- Plataforma social para accountability comunitario
- Transparencia en el cumplimiento de objetivos

## Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.1.5** - Framework de desarrollo de aplicaciones Java
  - Spring Boot Starter Web - Para desarrollo de APIs REST
  - Spring Boot Starter Test - Para testing automatizado
- **Maven** - Herramienta de gestión de dependencias y construcción

### Herramientas de Desarrollo
- **Maven 3.9+** - Gestión de dependencias y construcción
- **Git** - Control de versiones

## Requisitos del Sistema

### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior
- Git (para clonar el repositorio)

### Verificar Instalación
```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version
```

## Cómo Instalar y Ejecutar

### 1. Clonar el Repositorio
```bash
git clone https://github.com/Dieg0bl/Impulse.git
cd Impulse
```

### 2. Instalación de Dependencias
```bash
# Descargar dependencias y compilar
mvn clean install
```

### 3. Ejecutar la Aplicación

#### Opción A: Usando Maven
```bash
# Ejecutar la aplicación directamente con Maven
mvn spring-boot:run
```

#### Opción B: Usando JAR generado
```bash
# Primero construir el JAR
mvn clean package

# Ejecutar el JAR
java -jar target/impulse-0.0.1-SNAPSHOT.jar
```

### 4. Acceder a la Aplicación
Una vez iniciada la aplicación, estará disponible en:
```
http://localhost:8080
```

## Comandos Útiles

### Desarrollo
```bash
# Limpiar y compilar
mvn clean compile

# Ejecutar tests
mvn test

# Verificar el proyecto
mvn verify

# Limpiar archivos generados
mvn clean
```

### Empaquetado
```bash
# Crear JAR ejecutable
mvn package

# Saltar tests durante empaquetado
mvn package -DskipTests
```

## Estructura del Proyecto

```
Impulse/
├── pom.xml                 # Configuración Maven
├── README.md              # Documentación del proyecto
├── src/
│   ├── main/
│   │   ├── java/          # Código fuente principal
│   │   └── resources/     # Recursos de la aplicación
│   └── test/
│       └── java/          # Tests unitarios
└── target/                # Archivos generados (JAR, clases compiladas)
```

## Configuración

### Variables de Entorno
- `SERVER_PORT`: Puerto del servidor (por defecto: 8080)
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring Boot (desarrollo, producción, etc.)

### Configuración de la Aplicación
La configuración se encuentra en `src/main/resources/application.properties` o `application.yml`

## Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear un Pull Request

## Estado del Proyecto

🚧 **En Desarrollo Inicial** - Este proyecto está en sus etapas tempranas de desarrollo.

## Licencia

Este proyecto está bajo desarrollo y la licencia será definida próximamente.

## Contacto

Para preguntas o sugerencias sobre el proyecto Impulse, puedes contactar a través del repositorio de GitHub.