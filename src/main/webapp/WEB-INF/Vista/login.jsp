<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    // Escapa HTML para no inyectar texto del usuario directamente en la página
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión - Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.jsp">Resort Grand Line</a>
    </div>
</nav>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h5 class="card-title mb-0">Iniciar sesión</h5>
                </div>
                <div class="card-body p-4">

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) { %>
                        <div class="alert alert-danger py-2" role="alert"><%= esc(error) %></div>
                    <% } %>

                    <form action="LoginServlet" method="POST">
                        <div class="mb-3">
                            <label for="nombreUsuario" class="form-label fw-semibold">Usuario</label>
                            <input type="text" class="form-control" id="nombreUsuario" name="nombreUsuario"
                                   value="<%= esc((String) request.getAttribute("nombreUsuario")) %>"
                                   required autofocus>
                        </div>
                        <div class="mb-3">
                            <label for="contrasenia" class="form-label fw-semibold">Contraseña</label>
                            <input type="password" class="form-control" id="contrasenia" name="contrasenia" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2 mt-2 fw-semibold">Ingresar</button>
                    </form>

                </div>
            </div>
            <div class="text-center mt-3">
                <a href="index.jsp" class="text-muted small">&larr; Volver al inicio</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
