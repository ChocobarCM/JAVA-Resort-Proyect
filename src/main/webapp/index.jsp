<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Entities.Usuario" %>
<%!
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hover-card { transition: transform 0.3s ease; }
        .hover-card:hover { transform: translateY(-5px); }
    </style>
</head>
<body class="bg-light">

<!-- Barra de Navegación -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.jsp">Resort Grand Line</a>
        <div class="d-flex align-items-center">
            <% if (usuario == null) { %>
                <a href="LoginServlet" class="btn btn-outline-light btn-sm">Iniciar sesión</a>
            <% } else { %>
                <span class="navbar-text me-3">
                    <%= esc(usuario.getNombre()) %> <%= esc(usuario.getApellido()) %>
                    <span class="badge bg-secondary ms-1"><%= esc(usuario.getRol()) %></span>
                </span>
                <a href="LoginServlet?action=logout" class="btn btn-outline-light btn-sm">Cerrar sesión</a>
            <% } %>
        </div>
    </div>
</nav>

<div class="container py-5">

<% if (usuario == null) { %>

    <!-- ===== Bienvenida (invitado) ===== -->
    <div class="row justify-content-center text-center py-5">
        <div class="col-lg-8">
            <h1 class="display-5 fw-bold text-dark mb-3">Bienvenido al Resort Grand Line</h1>
            <p class="lead text-muted mb-4">
                Gestión de reservas, habitaciones y servicios de nuestro complejo turístico.
            </p>
            <a href="LoginServlet" class="btn btn-primary btn-lg px-5 fw-semibold">Iniciar sesión</a>
        </div>
    </div>

<% } else { %>

    <!-- ===== Panel principal (usuario logueado) ===== -->
    <div class="row mb-5 text-center">
        <div class="col">
            <h2 class="fw-bold text-dark">Hola, <%= esc(usuario.getNombre()) %></h2>
            <p class="text-muted fs-5">Seleccione el módulo al que desea acceder</p>
        </div>
    </div>

    <% if (!"ADMIN".equals(usuario.getRol())) { %>
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="alert alert-info text-center" role="alert">
                Su cuenta todavía no tiene módulos habilitados.
            </div>
        </div>
    </div>
    <% } else { %>
    <div class="row g-4 justify-content-center">
        <!-- Módulo Personas -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0 hover-card">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <h4 class="card-title fw-bold text-dark mb-3">Personas</h4>
                    <p class="card-text text-muted flex-grow-1">Gestión completa de usuarios, clientes y personal en la base de datos.</p>
                    <a href="PersonaServlet?action=list" class="btn btn-primary w-100 py-2 fw-semibold mt-3">Ingresar</a>
                </div>
            </div>
        </div>

        <!-- Módulo Habitaciones (pendiente) -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <h4 class="card-title fw-bold text-dark mb-3">Habitaciones</h4>
                    <p class="card-text text-muted flex-grow-1">Configuración de disponibilidad, tipos de cuartos y mantenimiento.</p>
                    <button class="btn btn-secondary w-100 py-2 fw-semibold mt-3" disabled>Próximamente</button>
                </div>
            </div>
        </div>

        <!-- Módulo Reservas (pendiente) -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <h4 class="card-title fw-bold text-dark mb-3">Reservas</h4>
                    <p class="card-text text-muted flex-grow-1">Control de asignaciones, turnos y panel de fechas ocupadas.</p>
                    <button class="btn btn-secondary w-100 py-2 fw-semibold mt-3" disabled>Próximamente</button>
                </div>
            </div>
        </div>
    </div>
    <% } %>

<% } %>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
