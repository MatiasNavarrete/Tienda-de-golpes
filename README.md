# 🛒 Tienda de Golpes - Arquitectura de Microservicios

Ecosistema distribuido y escalable desarrollado en *Java 21* con *Spring Boot, diseñado bajo un patrón de arquitectura limpia y comunicación asrónica/sincrónica entre servicios. Las bases de datos se gestionan de forma independiente mediante **Flyway Migrations*.

---

## 👥 Integrantes del Proyecto
* *Alexander Campos* (Desarrollador de Pedidos, Pagos, Envios y Reseñas)
* *Matías Navarrete* (Desarrollador de Usuarios, Notificaciones y Búsqueda)
* *Jose Romero* (Desarrollador de Productos, Inventario y Carrito)

---

## 🗺️ Mapa de Puertos y Arquitectura

El ecosistema se distribuye en los siguientes puertos locales para evitar colisiones y mantener el aislamiento de responsabilidades:

| Microservicio | Puerto | Base de Datos | Descripción |
| :--- | :---: | :--- | :--- |
| *ms-productos* | 8080 | db_productos | Catálogo central de artículos de la tienda. |
| *ms-usuarios* | 8081 | db_usuarios | Gestión de cuentas de usuario y credenciales. |
| *ms-carrito* | 8082 | db_carrito | Almacenamiento temporal de productos por cliente. |
| *ms-notificaciones*| 8083 | db_notificaciones | Historial de alertas de compra enviadas. |
| *ms-envios* | 8084 | - | Simulación de órdenes de despacho. |
| *ms-pagos* | 8085 | - | Pasarela interna de aprobación de transacciones. |
| *ms-pedido* | 8086 | db_pedido | Motor transaccional de compras y liquidación. |
| *ms-busqueda* | 8087 | db_busqueda | Motor descentralizado para filtros e indexación rápida. |
| *ms-resena* | 8088 | db_resena | Calificaciones y comentarios validados de clientes. |
| *ms-inventario* | 9090 | db_inventario | Control físico y stock de productos en tiempo real. |

---

## 🧪 Flujo de Verificación y Pruebas (Postman)

Para defender el proyecto ante la comisión, los endpoints principales se gatillan en el siguiente orden estratégico:

### 1. Cargar el Carrito (8082)
* *Endpoint:* POST http://localhost:8082/api/v1/carrito/{usuarioId}
* *Body (JSON):*
```json
{
  "productoId": 1,
  "cantidad": 2
}
