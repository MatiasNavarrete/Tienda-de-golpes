CREATE TABLE resenas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    comentario VARCHAR(255) NOT NULL,
    estrellas INT NOT NULL
);