Documentación de Arquitectura de Microservicios

Este documento describe la estructura central, los puertos de red y las responsabilidades de cada microservicio dentro del ecosistema de la aplicación. El proyecto está contenerizado y orquestado mediante Docker.
 Stack Tecnológico
Lenguaje: Java
Framework Principal: Spring Boot (v4.1.0)
Enrutamiento: Spring Cloud Gateway MVC
Documentación de API: Swagger / OpenAPI 3
Despliegue: Docker & Docker Compose
 Mapa de Red y Puertos
Microservicio Puerto Expuesto URL Base Interna Descripción Rápida
API Gateway     8080         http://localhost:8080  Puerta de enlace central.
Usuario         8081          /api/usuario/**      Gestión de identidades y accesos.
Búsqueda        8082          /api/busqueda/**     Catálogo, indexación y filtrado.
Notificaciones  8083          /api/notificaciones/**Sistema de mensajería y alertas.

 Detalle por Microservicio
1. API Gateway (api-gateway)
Actúa como el único punto de entrada público para las aplicaciones cliente (Frontend, Postman, etc.). Su objetivo es ocultar la complejidad de la red interna.
Puerto: 8080
Características Clave:
Implementado con Spring Cloud Gateway MVC puro (evitando la pila reactiva/WebFlux).
Enrutamiento programático estricto configurado en Java (GatewayConfig.java) para evitar fallos de lectura en YAML.
Intercepta peticiones y las redirige al contenedor correspondiente en la red interna de Docker.

2.Microservicio de Usuario (usuarios)
Encargado de la lógica de negocio relacionada con las cuentas de las personas que interactúan con el sistema.
Puerto: 8081
Prefijo de Ruta: /api/usuarios/
Responsabilidades: (A documentar por el equipo: ej. Registro, Autenticación, Perfiles).

3. Microservicio de Búsqueda (busqueda)
Núcleo del catálogo del sistema. Gestiona el registro de nuevos productos (como equipamiento) y proporciona búsquedas dinámicas para los usuarios.
Puerto: 8082
Prefijo de Ruta: /api/busqueda/
Características Clave:Nivel 3 de Madurez de Richardson (Implementa HATEOAS para auto-descubrimiento de URLs).
Conexión a base de datos relacional para persistencia de catálogo.
Endpoints Principales:
POST /admin/indexar: Recibe un JSON (nombre, categoría, precio, stock), guarda el artículo en la base de datos e inyecta enlaces de navegación (ej. catalogo-completo).
GET /productos: Filtra productos por nombre o criterio de búsqueda, devolviendo una colección estructurada con hipermedios narrativos.

4. Microservicio de Notificaciones (notificaciones)
Sistema encargado de la comunicación asíncrona o directa para avisos dentro de la plataforma.
Puerto: 8083
Prefijo de Ruta: /api/notificaciones/
Responsabilidades: (A documentar por el equipo: ej. Envío de correos, alertas de sistema, avisos de nuevo stock). 

Instrucciones de Despliegue Local
Para levantar todo el ecosistema desde cero sin usar la memoria caché (ideal tras realizar cambios estructurales), ejecutar en la raíz del proyecto:
docker-compose down
docker-compose build --no-cache
docker-compose up -d
