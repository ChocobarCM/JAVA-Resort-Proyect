-- =========================================================
-- BASE DE DATOS
-- =========================================================

CREATE DATABASE IF NOT EXISTS java_resort;
USE java_resort;

-- =========================================================
-- TABLA PERSONA
-- =========================================================

CREATE TABLE persona (
    idPersona INT AUTO_INCREMENT,
    dni INT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,

    CONSTRAINT PK_Persona
        PRIMARY KEY (idPersona),

    CONSTRAINT UQ_Persona_DNI
        UNIQUE (dni)
);

-- =========================================================
-- TABLA USUARIO
-- =========================================================

CREATE TABLE usuario (
    idPersona INT PRIMARY KEY,
    nombreUsuario VARCHAR(50) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',

    FOREIGN KEY (idPersona) REFERENCES persona(idPersona)
);

-- =========================================================
-- TABLA TIPO_HABITACION
-- =========================================================

CREATE TABLE Tipo_Habitacion (
    idTipoHab INT AUTO_INCREMENT,
    nomb VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    capacidad INT NOT NULL,
    precioNoche DECIMAL(10,2) NOT NULL,

    CONSTRAINT PK_Tipo_Habitacion
        PRIMARY KEY (idTipoHab),

    CONSTRAINT CHK_Tipo_Habitacion_Capacidad
        CHECK (capacidad > 0),

    CONSTRAINT CHK_Tipo_Habitacion_Precio
        CHECK (precioNoche >= 0)
);

-- =========================================================
-- TABLA HABITACION
-- =========================================================

CREATE TABLE Habitacion (
    idHab INT AUTO_INCREMENT,
    num VARCHAR(20) NOT NULL,
    piso INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    idTipoHab INT NOT NULL,

    CONSTRAINT PK_Habitacion
        PRIMARY KEY (idHab),

    CONSTRAINT UQ_Habitacion_Numero
        UNIQUE (num),

    CONSTRAINT FK_Habitacion_Tipo
        FOREIGN KEY (idTipoHab)
        REFERENCES Tipo_Habitacion(idTipoHab)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- =========================================================
-- TABLA SERVICIOS
-- =========================================================

CREATE TABLE Servicios (
    idServicio INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precioS DECIMAL(10,2) NOT NULL,

    CONSTRAINT PK_Servicios
        PRIMARY KEY (idServicio),

    CONSTRAINT CHK_Servicios_Precio
        CHECK (precioS >= 0)
);

-- =========================================================
-- TABLA RESERVA
-- =========================================================

CREATE TABLE Reserva (
    idReserva INT AUTO_INCREMENT,
    idUsuario INT NOT NULL,          -- huesped: persona/usuario que hizo la reserva
    idHab INT NOT NULL,
    fechaEntrada DATE NOT NULL,
    fechaSalida DATE NOT NULL,       -- dia de salida: la habitacion queda libre ese dia
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',   -- CONFIRMADA / CANCELADA
    precioTotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT PK_Reserva
        PRIMARY KEY (idReserva),

    CONSTRAINT FK_Reserva_Usuario
        FOREIGN KEY (idUsuario)
        REFERENCES usuario(idPersona)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Reserva_Habitacion
        FOREIGN KEY (idHab)
        REFERENCES Habitacion(idHab)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT CHK_Reserva_Fechas
        CHECK (fechaSalida > fechaEntrada),

    CONSTRAINT CHK_Reserva_Precio
        CHECK (precioTotal >= 0),

    -- acelera la busqueda de habitaciones disponibles por rango de fechas
    INDEX IDX_Reserva_Hab_Fechas (idHab, fechaEntrada, fechaSalida)
);

-- =========================================================
-- TABLA IMAGEN_HABITACION (varias imagenes por habitacion)
-- =========================================================

CREATE TABLE Imagen_Habitacion (
    idImagen INT AUTO_INCREMENT,
    idHab INT NOT NULL,
    nombreArchivo VARCHAR(100) NOT NULL,   -- solo el nombre (ej: 3f2a...c1.jpg), la carpeta la define GestorImagenes

    CONSTRAINT PK_Imagen_Habitacion
        PRIMARY KEY (idImagen),

    CONSTRAINT UQ_Imagen_Habitacion_Archivo
        UNIQUE (nombreArchivo),

    -- si se borra la habitacion, se borran sus filas de imagenes
    -- (los archivos fisicos los borra el servlet)
    CONSTRAINT FK_Imagen_Habitacion_Hab
        FOREIGN KEY (idHab)
        REFERENCES Habitacion(idHab)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
