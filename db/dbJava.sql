-- =========================================================
-- BASE DE DATOS
-- =========================================================

CREATE DATABASE IF NOT EXISTS java_resort;
USE java_resort;


-- =========================================================
-- TABLA PERSONA
-- =========================================================

CREATE TABLE Persona (
    idPersona INT AUTO_INCREMENT,
    dni INT NOT NULL,
    nombreComp VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL,

    CONSTRAINT PK_Persona
        PRIMARY KEY (idPersona),

    CONSTRAINT UQ_Persona_DNI
        UNIQUE (dni)
);


-- =========================================================
-- TABLA USUARIO
-- Hereda de Persona
-- =========================================================

CREATE TABLE Usuario (
    idPersona INT,

    CONSTRAINT PK_Usuario
        PRIMARY KEY (idPersona),

    CONSTRAINT FK_Usuario_Persona
        FOREIGN KEY (idPersona)
        REFERENCES Persona(idPersona)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- =========================================================
-- TABLA TIPO_HABITACION
-- =========================================================

CREATE TABLE Tipo_Habitacion (
    idTipoHab INT AUTO_INCREMENT,
    nomb VARCHAR(100) NOT NULL,
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
    idUsuario INT NOT NULL,
    idHab INT NOT NULL,

    CONSTRAINT PK_Reserva
        PRIMARY KEY (idReserva),

    CONSTRAINT FK_Reserva_Usuario
        FOREIGN KEY (idUsuario)
        REFERENCES Usuario(idPersona)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT FK_Reserva_Habitacion
        FOREIGN KEY (idHab)
        REFERENCES Habitacion(idHab)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- =========================================================
-- FIN DEL SCRIPT
-- =========================================================