<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<!DOCTYPE html>
<html lang="es" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear cuenta - Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/onepiece.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-op">
    <div class="container">
        <a class="navbar-brand fw-bold text-op-gold" href="index.jsp">RESORT GRAND LINE</a>
    </div>
</nav>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card card-op-static">
                <div class="card-header py-3 text-center">
                    <h5 class="card-title mb-0">Crear cuenta</h5>
                </div>
                <div class="card-body p-4">

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) { %>
                        <div class="alert alert-danger py-2" role="alert"><%= esc(error) %></div>
                    <% } %>

                    <form action="RegisterServlet" method="POST" id="formRegistro">

                        <h6 class="text-op-gold text-uppercase small fw-bold mb-3">Datos personales</h6>

                        <div class="row">
                            <div class="col-sm-6 mb-3">
                                <label for="nombre" class="form-label fw-semibold">Nombre</label>
                                <input type="text" class="form-control form-control-op" id="nombre" name="nombre"
                                       maxlength="50" required autofocus
                                       value="<%= esc((String) request.getAttribute("nombre")) %>">
                            </div>
                            <div class="col-sm-6 mb-3">
                                <label for="apellido" class="form-label fw-semibold">Apellido</label>
                                <input type="text" class="form-control form-control-op" id="apellido" name="apellido"
                                       maxlength="50" required
                                       value="<%= esc((String) request.getAttribute("apellido")) %>">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-5 mb-3">
                                <label for="dni" class="form-label fw-semibold">DNI</label>
                                <input type="text" class="form-control form-control-op" id="dni" name="dni"
                                       inputmode="numeric" pattern="\d{7,8}" maxlength="8"
                                       title="7 u 8 dígitos, sin puntos" required
                                       value="<%= esc((String) request.getAttribute("dni")) %>">
                            </div>
                            <div class="col-sm-7 mb-3">
                                <label for="email" class="form-label fw-semibold">Email</label>
                                <input type="email" class="form-control form-control-op" id="email" name="email"
                                       maxlength="100" required
                                       value="<%= esc((String) request.getAttribute("email")) %>">
                            </div>
                        </div>

                        <hr class="my-4 border-warning">
                        <h6 class="text-op-gold text-uppercase small fw-bold mb-3">Datos de acceso</h6>

                        <div class="mb-3">
                            <label for="nombreUsuario" class="form-label fw-semibold">Usuario</label>
                            <input type="text" class="form-control form-control-op" id="nombreUsuario" name="nombreUsuario"
                                   pattern="[A-Za-z0-9._\-]{3,30}" minlength="3" maxlength="30" required
                                   title="3 a 30 caracteres: letras, números, punto, guion o guion bajo"
                                   value="<%= esc((String) request.getAttribute("nombreUsuario")) %>">
                            <div class="form-text text-op-muted">Entre 3 y 30 caracteres: letras, números, punto, guion o guion bajo.</div>
                        </div>
                        <div class="row">
                            <div class="col-sm-6 mb-3">
                                <label for="contrasenia" class="form-label fw-semibold">Contraseña</label>
                                <input type="password" class="form-control form-control-op" id="contrasenia" name="contrasenia"
                                       minlength="8" maxlength="100" required autocomplete="new-password">
                                <div class="form-text text-op-muted">Mínimo 8 caracteres.</div>
                            </div>
                            <div class="col-sm-6 mb-3">
                                <label for="confirmacion" class="form-label fw-semibold">Repetir contraseña</label>
                                <input type="password" class="form-control form-control-op" id="confirmacion" name="confirmacion"
                                       minlength="8" maxlength="100" required autocomplete="new-password">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-op-gold w-100 py-2 mt-2">Crear cuenta</button>
                    </form>

                </div>
            </div>
            <div class="text-center mt-3">
                <p class="mb-2 small text-op-muted">¿Ya tiene cuenta? <a href="LoginServlet" class="fw-semibold text-op-gold">Iniciar sesión</a></p>
                <a href="index.jsp" class="text-op-muted small">&larr; Volver al inicio</a>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {
        var pass = document.getElementById('contrasenia');
        var conf = document.getElementById('confirmacion');
        function chequear() {
            conf.setCustomValidity(conf.value !== pass.value ? 'Las contraseñas no coinciden' : '');
        }
        pass.addEventListener('input', chequear);
        conf.addEventListener('input', chequear);
    })();
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>