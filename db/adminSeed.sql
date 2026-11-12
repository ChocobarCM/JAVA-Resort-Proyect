-- =========================================================
-- USUARIO ADMINISTRADOR INICIAL
-- Usuario: admin    Contraseña inicial: Admin123!
-- (la contraseña se guarda hasheada con PBKDF2, ver logic.PasswordUtil)
-- Es seguro ejecutarlo más de una vez: no duplica nada.
-- IMPORTANTE: cambiar la contraseña antes de publicar el sitio.
-- =========================================================

USE java_resort;

INSERT IGNORE INTO persona (dni, nombre, apellido, email)
VALUES (11111111, 'Admin', 'Sistema', 'admin@resort.com');

INSERT IGNORE INTO usuario (idPersona, nombreUsuario, contrasenia, rol, estado)
SELECT idPersona,
       'admin',
       '120000:hc5gcMOapkfmzuu5BHy2tg==:TqsJhdXqoR51rq2Op2w+Aovf/qYFckMhP1O9hDNxsUU=',
       'ADMIN',
       'ACTIVO'
FROM persona
WHERE dni = 11111111;