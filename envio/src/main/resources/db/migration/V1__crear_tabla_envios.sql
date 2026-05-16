CREATE TABLE envios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pedido_id BIGINT,
    direccion_destino VARCHAR(255),
    estado_envio VARCHAR(50)
);