# Documentación de Arquitectura de Microservicios

Este repositorio contiene el ecosistema de microservicios de la aplicación, el cual está completamente contenerizado y orquestado mediante **Docker** y **Docker Compose**. El documento describe la estructura central, los puertos de red y las responsabilidades de cada componente.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java
* **Framework Principal:** Spring Boot (v4.1.0)
* **Enrutamiento:** Spring Cloud Gateway MVC
* **Documentación de API:** Swagger / OpenAPI 3
* **Despliegue y Orquestación:** Docker & Docker Compose

---

## 🌐 Mapa de Red y Puertos

El API Gateway actúa como el único punto de entrada expuesto al exterior. Los demás microservicios se comunican de forma interna o a través del ruteo del Gateway.

| Microservicio | Puerto Expuesto | URL Base Interna / Prefijo | Descripción Rápida |
| :--- | :---: | :--- | :--- |
| **API Gateway** | `8080` | `http://localhost:8080` | Puerta de enlace central y enrutador. |
| **Usuario** | `8081` | `/api/usuarios/**` | Gestión de identidades y accesos. |
| **Búsqueda** | `8082` | `/api/busqueda/**` | Catálogo, indexación y filtrado. |
| **Notificaciones** | `8083` | `/api/notificaciones/**` | Sistema de mensajería y alertas. |

---

## 🧩 Detalle por Microservicio

### 1. API Gateway (`api-gateway`)
Actúa como el único punto de entrada público para las aplicaciones cliente (Frontend, Postman, etc.), ocultando la complejidad de la red interna.
* **Puerto:** `8080`
* **Características Clave:**
    * Implementado con **Spring Cloud Gateway MVC** puro (evitando la pila reactiva/WebFlux).
    * Enrutamiento programático estricto configurado en Java (`GatewayConfig.java`) para evitar fallos de lectura en archivos YAML.
    * Intercepta las peticiones y las redirige al contenedor correspondiente en la red interna de Docker.

### 2. Microservicio de Usuario (`usuarios`)
Encargado de la lógica de negocio relacionada con las cuentas de las personas que interactúan con el sistema.
* **Puerto:** `8081`
* **Prefijo de Ruta:** `/api/usuarios/`
* **Responsabilidades** *(Por documentar por el equipo)*:
    * [ ] Registro de usuarios.
    * [ ] Autenticación y Autorización.
    * [ ] Gestión de Perfiles.

### 3. Microservicio de Búsqueda (`busqueda`)
Núcleo del catálogo del sistema. Gestiona el registro de nuevos productos y proporciona búsquedas dinámicas.
* **Puerto:** `8082`
* **Prefijo de Ruta:** `/api/busqueda/`
* **Características Clave:**
    * **Nivel 3 de Madurez de Richardson:** Implementa HATEOAS para el auto-descubrimiento de URLs mediante hipermedios.
    * Conexión a base de datos relacional para la persistencia del catálogo.
* **Endpoints Principales:**
    * `POST /admin/indexar`: Recibe un JSON (`nombre`, `categoría`, `precio`, `stock`), guarda el artículo en la base de datos e inyecta enlaces de navegación (ej. `catalogo-completo`).
    * `GET /productos`: Filtra productos por nombre o criterio de búsqueda, devolviendo una colección estructurada con hipermedios narrativos.

### 4. Microservicio de Notificaciones (`notificaciones`)
Sistema encargado de la comunicación asíncrona o directa para avisos dentro de la plataforma.
* **Puerto:** `8083`
* **Prefijo de Ruta:** `/api/notificaciones/`
* **Responsabilidades** *(Por documentar por el equipo)*:
    * [ ] Envío de correos electrónicos.
    * [ ] Alertas del sistema.
    * [ ] Avisos de nuevo stock.

---

## 🚀 Instrucciones de Despliegue Local

Para levantar todo el ecosistema desde cero sin usar la memoria caché (ideal tras realizar cambios estructurales o de código), ejecuta el siguiente bloque de comandos en la raíz del proyecto donde se encuentra el archivo `docker-compose.yml`:

```bash
# Apagar contenedores previos y limpiar volúmenes/redes locales
docker-compose down

# Reconstruir las imágenes desde cero omitiendo el caché
docker-compose build --no-cache

# Levantar los servicios en segundo plano (detached mode)
docker-compose up -d
