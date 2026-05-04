CREATE TABLE IF NOT EXISTS items_carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_carrito FOREIGN KEY (carrito_id) REFERENCES carrito(id)
);