# Documentación de Arquitectura de Microservicios

Este repositorio contiene el ecosistema de microservicios de la aplicación, el cual está completamente contenerizado y orquestado mediante **Docker** y **Docker Compose**. El documento describe la estructura central, los puertos de red y las responsabilidades de cada componente desarrollado por Alexander Campos.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java (v21)
* **Framework Principal:** Spring Boot (v3.3.5 / v4.0.6)
* **Enrutamiento y Descubrimiento:** Spring Cloud Gateway & Netflix Eureka
* **Persistencia y Migraciones:** MySQL, Spring Data JPA, Hibernate & Flyway
* **Documentación de API:** Swagger / OpenAPI 3
* **Despliegue y Orquestación:** Docker & Docker Compose

---

## 🌐 Mapa de Red y Puertos

El API Gateway actúa como el único punto de entrada expuesto al exterior para el flujo transaccional. Los demás microservicios se comunican de forma interna, se descubren mediante Eureka, o a través del ruteo del Gateway.

| Microservicio | Puerto Expuesto | URL Base Interna / Prefijo | Descripción Rápida |
| :--- | :---: | :--- | :--- |
| **API Gateway** | `8097` | `http://localhost:8097` | Puerta de enlace central y enrutador. |
| **Eureka Server** | `8761` | `http://localhost:8761` | Servidor de descubrimiento de instancias. |
| **Pedidos** | `8086` | `/api/pedidos/**` | Lógica de órdenes de compra y estados. |
| **Pagos** | `8085` | `/api/pagos/**` | Procesamiento de transacciones financieras. |
| **Envíos** | `8084` | `/api/envios/**` | Logística, distribución y seguimiento. |
| **Reseñas** | `8088` | `/api/resenas/**` | Feedback, calificaciones y comentarios. |

---

## 🧩 Detalle por Microservicio

### 1. API Gateway (`api-gateway`)
Actúa como el único punto de entrada público para las aplicaciones cliente (Frontend, Postman, etc.), ocultando la complejidad de la red interna.
* **Puerto:** `8097`
* **Características Clave:**
    * Implementado con Spring Cloud Gateway.
    * Enrutamiento estricto configurado mediante archivos YAML para redirigir el tráfico.
    * Intercepta las peticiones y las redirige a las instancias registradas en Eureka.

### 2. Eureka Server (`eureka-server`)
Actúa como el registro central de la arquitectura distribuida, permitiendo el auto-descubrimiento de los servicios.
* **Puerto:** `8761`
* **Características Clave:**
    * Monitorea el estado de salud de cada microservicio (UP/DOWN).
    * Evita el acoplamiento de IPs estáticas en la comunicación interna.

### 3. Microservicio de Pedidos (`ms-pedido`)
Encargado de la creación, persistencia y control de flujo de las órdenes de compra de la tienda.
* **Puerto:** `8086`
* **Prefijo de Ruta:** `/api/pedidos/`
* **Responsabilidades** *(Por documentar por el equipo)*:
    * [ ] Registro y almacenamiento de nuevos pedidos.
    * [ ] Actualización de estados del ciclo de compra.
    * [ ] Consulta de historial de órdenes por cliente.

### 4. Microservicio de Pagos (`ms-pago`)
Módulo encargado de interactuar de forma segura con las transacciones monetarias derivadas de los pedidos.
* **Puerto:** `8085`
* **Prefijo de Ruta:** `/api/pagos/`
* **Responsabilidades** *(Por documentar por el equipo)*:
    * [ ] Validación y procesamiento de pagos.
    * [ ] Control de comprobantes financieros.

### 5. Microservicio de Envíos (`ms-envio`)
Subsistema logístico responsable de coordinar la entrega y distribución de los productos.
* **Puerto:** `8084`
* **Prefijo de Ruta:** `/api/envios/`
* **Responsabilidades** *(Por documentar por el equipo)*:
    * [ ] Asignación de rutas y transportistas.
    * [ ] Actualización del tracking de despacho.

### 6. Microservicio de Reseñas (`ms-resena`)
Maneja el feedback, comentarios y valoraciones de los usuarios tras concretar sus compras.
* **Puerto:** `8088`
* **Prefijo de Ruta:** `/api/resenas/`
* **Características Clave:**
    * **Comunicación Remota:** Utiliza `WebClient` para consumir el endpoint de `ms-pedido` de forma no bloqueante, asegurando que la reseña corresponda a un pedido válido.
    * **Migraciones Automatizadas:** Integra Flyway para inicializar y versionar las tablas en la base de datos MySQL.
* **Endpoints Principales:**
    * `POST /api/resenas/crear`: Recibe la estructura de la reseña, valida las reglas de negocio (ej. estrellas, tamaño del comentario), verifica remotamente el pedido y persiste los datos.
    * `GET /api/resenas/listar`: Retorna el catálogo completo de valoraciones guardadas en el sistema.

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
