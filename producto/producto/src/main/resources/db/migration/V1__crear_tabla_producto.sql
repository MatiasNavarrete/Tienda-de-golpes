CREATE TABLE producto(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200),
    precio INT,
    stock INT NOT NULL
);
