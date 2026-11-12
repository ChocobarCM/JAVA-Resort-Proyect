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
    <title>Iniciar sesión - Resort Grand Line</title>
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
        <div class="col-md-6 col-lg-4">
            <div class="card card-op-static">
                <div class="card-header py-3 text-center">
                    <h5 class="card-title mb-0">Iniciar sesión</h5>
                </div>
                <div class="card-body p-4">

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) { %>
                        <div class="alert alert-danger py-2" role="alert"><%= esc(error) %></div>
                    <% } %>
                    <% if ("1".equals(request.getParameter("registrado"))) { %>
                        <div class="alert alert-success py-2" role="alert">Cuenta creada correctamente. Ya puede iniciar sesión.</div>
                    <% } %>

                    <form action="LoginServlet" method="POST">
                        <div class="mb-3">
                            <label for="nombreUsuario" class="form-label fw-semibold">Usuario</label>
                            <input type="text" class="form-control form-control-op" id="nombreUsuario" name="nombreUsuario"
                                   value="<%= esc((String) request.getAttribute("nombreUsuario")) %>"
                                   required autofocus>
                        </div>
                        <div class="mb-3">
                            <label for="contrasenia" class="form-label fw-semibold">Contraseña</label>
                            <input type="password" class="form-control form-control-op" id="contrasenia" name="contrasenia" required>
                        </div>
                        <button type="submit" class="btn btn-op-gold w-100 py-2 mt-2">Ingresar</button>
                    </form>

                </div>
            </div>
            <div class="text-center mt-3">
                <p class="mb-2 small text-op-muted">¿No tiene cuenta? <a href="RegisterServlet" class="fw-semibold text-op-gold">Crear una cuenta</a></p>
                <a href="index.jsp" class="text-op-muted small">&larr; Volver al inicio</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>