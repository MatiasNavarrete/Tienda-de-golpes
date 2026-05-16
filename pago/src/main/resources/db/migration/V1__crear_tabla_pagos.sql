CREATE TABLE pagos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pedido_id BIGINT,
    monto DOUBLE,
    metodo_pago VARCHAR(50),
    estado VARCHAR(50)
);