# 🛒 Sistema Integrado de Microservicios - Tienda de Golpes y Reseñas

## 📝 Descripción del Proyecto
Este proyecto consiste en una arquitectura distribuida basada en microservicios independientes para la gestión de una plataforma de comercio electrónico ("Tienda de Golpes"). El ecosistema resuelve de extremo a extremo el ciclo de venta, permitiendo la administración de cuentas de usuario, catálogo indexado de productos con hipermedios, orquestación de pedidos, procesamiento seguro de pagos, logística de envíos y un sistema automatizado de feedback y reseñas con validación reactiva inter-servicio.

## 👥 Integrantes del Equipo y Roles Técnicos
* **Alexander Campos**: Desarrollador del Módulo de Operaciones, Transacciones e Infraestructura (Servicios de Pedidos, Pagos, Envíos, Reseñas, Servidor Eureka y API Gateway Transaccional).
* **Matias Navarrete**: Desarrollador del Módulo Core, Catálogo y Mensajería (Servicios de Usuario, Búsqueda HATEOAS, Notificaciones y API Gateway Core).

---

## 🛠️ Stack Tecnológico
* **Lenguaje:** Java 21 / Java 24
* **Framework Principal:** Spring Boot
* **Persistencia:** JPA + Hibernate con bases de datos MySQL independientes
* **Estrategia de Migración:** Flyway para control de versiones de esquemas SQL
* **Documentación:** Swagger / OpenAPI 3
* **Orquestación y Contenedores:** Docker & Docker Compose

---

## 🚀 Listado de Microservicios e Infraestructura

El ecosistema está compuesto por los siguientes componentes autónomos distribuidos por puertos:

### Módulo Transaccional (Alexander Campos)
1. **Eureka Server (`eureka-server`)** [Puerto `8761`]: Servidor de descubrimiento y registro dinámico de instancias.
2. **API Gateway Transaccional (`api-gateway`)** [Puerto `8097`]: Enrutador perimetral del flujo transaccional.
3. **ms-pedido** [Puerto `8086`]: Lógica de negocio para la generación y estados de órdenes de compra.
4. **ms-pago** [Puerto `8085`]: Gestión y confirmación de transacciones financieras de los pedidos.
5. **ms-envio** [Puerto `8084`]: Logística de despachos, asignación y seguimiento de entregas.
6. **ms-resena** [Puerto `8088`]: Sistema de calificación y feedback. Utiliza `WebClient` para validar en tiempo real la existencia de un pedido en el puerto `8086` antes de guardar la reseña.

### Módulo Core y Catálogo (Matias Navarrete)
7. **API Gateway Core (`api-gateway-core`)** [Puerto `8080`]: Punto de entrada centralizado puro implementado con Spring Cloud Gateway MVC.
8. **ms-usuario** [Puerto `8081`]: Gestión de cuentas de usuario, identidades y control de accesos.
9. **ms-busqueda** [Puerto `8082`]: Catálogo e indexación de productos. Implementa el Nivel 3 de Madurez de Richardson (HATEOAS) inyectando hipermedios relacionales.
10. **ms-notificaciones** [Puerto `8083`]: Emisión de alertas asíncronas, correos electrónicos y avisos de stock del sistema.

---

## 🧭 Rutas Principales del API Gateway

Las solicitudes externas se unifican a través de los Gateways y se redirigen de forma semántica hacia la red interna de contenedores:

### Gateway Transaccional (Puerto 8097)
* `GET /api/pedidos/**` ➔ Redirección interna hacia `ms-pedido`
* `POST /api/pagos
