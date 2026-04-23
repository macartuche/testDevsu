CREATE TABLE persona (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    genero VARCHAR(50),
    edad INTEGER,
    identificacion VARCHAR(50) UNIQUE NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(50)
);

CREATE TABLE cliente (
    cliente_id BIGSERIAL PRIMARY KEY,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    id INTEGER UNIQUE REFERENCES persona(id)
);

CREATE TABLE cuenta (
    id SERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) UNIQUE NOT NULL,
    tipo_cuenta VARCHAR(50),
    saldo_inicial DECIMAL(10, 2) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    cliente_id INTEGER NOT NULL REFERENCES cliente(id)
);

CREATE TABLE movimiento (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    saldo DECIMAL(10, 2) NOT NULL,
    cuenta_id INTEGER NOT NULL REFERENCES cuenta(id)
);

-- Datos de ejemplo (basados en tablas del documento de prueba)
INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono)
VALUES ('Jose Lema', 'Masculino', 30, '0987654321', 'Otavalo sn y principal', '098254785');
INSERT INTO cliente (id, cliente_id, contrasena, estado)
VALUES (1, 1, '1234', true);

INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono)
VALUES ('Marianela Montalvo', 'Femenino', 25, '1234567890', 'Amazonas y NNUU', '097548965');
INSERT INTO cliente (id, cliente_id, contrasena, estado)
VALUES (2, 2, '5678', true);

INSERT INTO persona (nombre, genero, edad, identificacion, direccion, telefono)
VALUES ('Juan Osorio', 'Masculino', 40, '0123456789', '13 junio y Equinoccial', '098874587');
INSERT INTO cliente (id, cliente_id, contrasena, estado)
VALUES (3, 3, '1245', true);

-- Cuentas
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('478758', 'Ahorros', 2000, true, 1);
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('225487', 'Corriente', 100, true, 2);
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('495878', 'Ahorros', 0, true, 3);
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id)
VALUES ('496825', 'Ahorros', 540, true, 2);
