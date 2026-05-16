CREATE TABLE pedidos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    producto_id VARCHAR(255),
    cantidad INT,
    precio_total DOUBLE
);