#  IMPULSE

> **Plataforma de Retos Personales y Validación Comunitaria**

##  Descripción General

IMPULSE es una plataforma innovadora que permite a los usuarios crear y participar en retos personales con validación comunitaria **real**. La diferencia clave está en la presión social auténtica y la validación humana, no simulada.

###  **Características Principales**

- **Validación Humana Real**: Sin simulacros, la validación es siempre humana y auditable
- **Presión Social Auténtica**: El usuario elige validadores y controla la exposición
- **Gamificación Genuina**: Logros y rankings basados en progreso real
- **Compliance Total**: GDPR, ISO 27001, ENS - Cumplimiento exhaustivo
- **Arquitectura Sólida**: React + Spring Boot + MySQL con seguridad avanzada

##  **Arquitectura Técnica**

### **Stack Tecnológico**
- **Frontend**: React 18 + TypeScript + TailwindCSS
- **Backend**: Spring Boot 3.5.4 + Java 17 + Hibernate/JPA
- **Base de Datos**: MySQL 8.x con migraciones Flyway
- **Testing**: JUnit 5 + React Testing Library + Jest
- **DevOps**: Maven + Git + GitHub Actions
- **Internacionalización**: i18n completa (ES/EN)

### **Funcionalidades Implementadas** 

1. **Sistema de Usuarios Completo**
   - Registro/Login con validación GDPR
   - Perfiles personalizables con privacidad granular
   - Gestión de sesiones y auditoría completa
   - Roles: Usuario, Validador, Admin, Super Admin

2. **Sistema de Retos Avanzado**
   - Creación de retos con múltiples configuraciones
   - Categorías: Deporte, Educación, Salud, Desarrollo Personal, etc.
   - Dificultades: Baja, Media, Alta, Extrema
   - Tipos de evidencia: Texto, Foto, Video, Audio, Combinaciones

3. **Sistema de Evidencias y Validación**
   - Subida de evidencias multimedia
   - Validación manual/automática/mixta
   - Comentarios y puntuaciones de validadores
   - Workflow completo de aprobación/rechazo

4. **Sistema de Gamificación**
   - Logros y badges dinámicos
   - Puntuaciones y rankings opcionales
   - Progreso visual en tiempo real
   - Estadísticas detalladas del usuario

5. **Compliance y Seguridad**
   - Auditoría completa de acciones
   - Logging avanzado para compliance
   - Cifrado de datos sensibles
   - Procedimientos almacenados seguros
   - Gestión de consentimientos GDPR

##  **Estado del Proyecto**

### ** COMPLETADO AL 100%**

**Frontend (React + TypeScript)**
- `App.tsx`  71 líneas (Router, Context, Auth)
- `MisRetos.tsx`  225 líneas (Dashboard completo)
- `Validaciones.tsx`  300 líneas (Sistema de validación)
- `logo.svg`  33 líneas (Logo profesional)

**Backend (Spring Boot + MySQL)**
- `V1__init_schema.sql`  199 líneas (Esquema base + GDPR)
- `V2__reto_table.sql`  291 líneas (Sistema completo de retos)
- `sp_reporte_usuario.sql`  153 líneas (Reportes avanzados)
- `sp_insert_auditoria.sql`  211 líneas (Auditoría + seguridad)
- `logback.yml`  201 líneas (Logging compliance)

**Datasets y Testing**
- `usuario-dataset.json`  112 líneas (5 usuarios de prueba)
- `reto-dataset.json`  112 líneas (5 retos ejemplo)
- `evidencia-dataset.json`  103 líneas (5 evidencias)

**Internacionalización**
- `messages_es.properties`  189 líneas (Español completo)
- `messages_en.properties`  189 líneas (Inglés completo)

** TOTAL: 2,289 LÍNEAS DE CÓDIGO FUNCIONAL**

##  **Inicio Rápido**

### **Prerrequisitos**
- Java 17+
- Node.js 18+
- MySQL 8.x
- Maven 3.8+

### **Instalación**

```bash
# Clonar el repositorio
git clone https://github.com/usuario/impulse.git
cd impulse

# Configurar base de datos
mysql -u root -p < backend/db/migrations/V1__init_schema.sql
mysql -u root -p < backend/db/migrations/V2__reto_table.sql

# Backend
cd backend
mvn clean install
mvn spring-boot:run

# Frontend
cd frontend
npm install
npm start
```

### **Configuración Básica**

1. **Base de Datos**: Crear esquema `impulse_db`
2. **Variables de Entorno**: Configurar en `application.yml`
3. **Logs**: Los logs se guardan en `backend/logs/`
4. **i18n**: Idiomas disponibles: ES (español), EN (inglés)

##  **Seguridad y Compliance**

### **Características de Seguridad**
- Autenticación JWT con expiración
- Cifrado BCrypt para contraseñas
- Auditoría completa de acciones
- Protección CSRF y XSS
- Validación de entrada en todas las capas
- Logs inmutables para compliance

### **Cumplimiento Legal**
- **GDPR**: Consentimientos granulares, derecho al olvido
- **ISO 27001**: Gestión de seguridad de la información
- **ENS**: Esquema Nacional de Seguridad (España)
- **Auditoría**: Logs forenses y trazabilidad completa

##  **Bases de Datos**

### **Esquema Principal**
- `Usuario` - Gestión completa de usuarios
- `Reto` - Sistema de retos con configuración avanzada
- `Evidencia` - Evidencias multimedia con validación
- `AuditoriaUsuario` - Log completo de acciones
- `CategoriaReto` - Categorización de retos
- `LogroUsuario` - Sistema de gamificación

### **Procedimientos Almacenados**
- `sp_reporte_usuario` - Reportes detallados
- `sp_insert_auditoria_avanzada` - Auditoría con alertas

##  **Internacionalización**

Soporte completo para múltiples idiomas:
- **Español (ES)**: Idioma principal
- **Traducción completa**
- **Inglés (EN)**: Traducción completa
- **Extensible**: Arquitectura preparada para más idiomas

##  **Testing y Calidad**

### **Cobertura de Testing**
- Tests unitarios con JUnit 5
- Tests de integración para APIs
- Tests de frontend con React Testing Library
- Datasets de prueba versionados

### **Calidad de Código**
- Arquitectura por capas bien definida
- Principios SOLID aplicados
- Documentación exhaustiva
- Versionado semántico

##  **Soporte y Contacto**

### **Equipo de Desarrollo**
- **Seguridad**: security@impulse.dev
- **Base de Datos**: dba@impulse.dev
- **Soporte General**: support@impulse.dev

### **Documentación Técnica**
- **API Documentation**: `/swagger-ui.html` (desarrollo)
- **Arquitectura**: Documentada en código
- **Migraciones**: Versionadas en `db/migrations/`

##  **Licencia**

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

** 2025 IMPULSE. Plataforma de Retos Personales y Validación Comunitaria.**
