<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entities.Usuario" %>
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
<html lang="es" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="css/onepiece.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-op">
    <div class="container">
        <a class="navbar-brand fw-bold text-op-gold" href="index.jsp">RESORT GRAND LINE</a>
        <div class="d-flex align-items-center">
            <% if (usuario == null) { %>
                <a href="LoginServlet" class="btn btn-outline-op btn-sm">Iniciar sesión</a>
            <% } else { %>
                <span class="navbar-text me-3 text-light">
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

    <div class="row justify-content-center text-center py-5">
        <div class="col-lg-8">
            <div class="card card-op-static p-5">
                <h1 class="display-5 fw-bold mb-3">Bienvenido al Resort Grand Line</h1>
                <p class="lead text-op-muted mb-4">
                    Gestión de reservas, camarotes y servicios de nuestro complejo turístico en el Nuevo Mundo.
                </p>
                <div>
                    <a href="LoginServlet" class="btn btn-op-gold btn-lg px-5">Iniciar sesión</a>
                </div>
            </div>
        </div>
    </div>

<% } else { %>

    <div class="row mb-5 text-center">
        <div class="col">
            <h2 class="fw-bold display-6">HOLA, <%= esc(usuario.getNombre()).toUpperCase() %></h2>
            <p class="text-op-muted fs-5">Seleccione el módulo de la bitácora al que desea acceder</p>
        </div>
    </div>

    <% if (!"ADMIN".equals(usuario.getRol())) { %>
    <div class="row g-4 justify-content-center">
        <div class="col-md-4">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-journal-bookmark text-op-gold display-5 mb-3"></i>
                    <h4 class="card-title fw-bold mb-3">Mis Reservas</h4>
                    <p class="card-text text-op-muted flex-grow-1">Consulte el estado y el detalle de sus reservas.</p>
                    <button class="btn btn-secondary w-100 py-2 fw-semibold mt-3" disabled>Próximamente</button>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-calendar-check text-op-gold display-5 mb-3"></i>
                    <h4 class="card-title fw-bold mb-3">Reservar</h4>
                    <p class="card-text text-op-muted flex-grow-1">Elija sus fechas y vea los camarotes disponibles.</p>
                    <button class="btn btn-secondary w-100 py-2 fw-semibold mt-3" disabled>Próximamente</button>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-person-badge text-op-gold display-5 mb-3"></i>
                    <h4 class="card-title fw-bold mb-3">Perfil</h4>
                    <p class="card-text text-op-muted flex-grow-1">Revise y actualice los datos de su cuenta.</p>
                    <button class="btn btn-secondary w-100 py-2 fw-semibold mt-3" disabled>Próximamente</button>
                </div>
            </div>
        </div>
    </div>
    <% } else { %>
    <div class="row g-4 justify-content-center">
        <div class="col-md-6 col-lg-3">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-person-vcard text-op-gold display-5 mb-3"></i>
                    <h5 class="card-title fw-bold mb-3">TRIPULANTES Y HUÉSPEDES</h5>
                    <p class="card-text text-op-muted flex-grow-1">Registro de piratas, marines y huéspedes en la bitácora del resort.</p>
                    <a href="PersonaServlet?action=list" class="btn btn-op-gold w-100 py-2 mt-3">
                        <i class="bi bi-compass me-1"></i> Ingresar
                    </a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-3">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-door-open text-op-gold display-5 mb-3"></i>
                    <h5 class="card-title fw-bold mb-3">CAMAROTES Y SUITES</h5>
                    <p class="card-text text-op-muted flex-grow-1">Configuración de disponibilidad, estado de cuartos y mantenimiento.</p>
                    <a href="HabitacionServlet?action=list" class="btn btn-op-gold w-100 py-2 mt-3">
                        <i class="bi bi-key me-1"></i> Ingresar
                    </a>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-3">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-gem text-op-gold display-5 mb-3"></i>
                    <h5 class="card-title fw-bold mb-3">CATEGORÍAS DE CAMAROTE</h5>
                    <p class="card-text text-op-muted flex-grow-1">Gestión de capacidades de tripulación y tarifas en Berries por noche.</p>
                    <a href="TipoHabitacionServlet?action=list" class="btn btn-op-gold w-100 py-2 mt-3">
                        <i class="bi bi-coin me-1"></i> Ingresar
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card card-op h-100">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <i class="bi bi-calendar2-week text-op-gold display-5 mb-3"></i>
                    <h5 class="card-title fw-bold mb-3">RESERVAS</h5>
                    <p class="card-text text-op-muted flex-grow-1">Control de asignaciones, turnos y panel de fechas ocupadas.</p>
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