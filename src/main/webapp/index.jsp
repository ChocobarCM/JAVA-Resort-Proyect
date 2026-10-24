<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel Principal - PeceraWeb</title>
    <!-- CDN Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hover-card:hover {
            transform: translateY(-5px);
            transition: transform 0.3s ease;
        }
        .hover-card {
            transition: transform 0.3s ease;
        }
    </style>
</head>
<body class="bg-light">

<!-- Barra de Navegación -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.jsp">PeceraWeb</a>
    </div>
</nav>

<div class="container py-5">
    <div class="row mb-5 text-center">
        <div class="col">
            <h2 class="fw-bold text-dark">Bienvenido al Sistema</h2>
            <p class="text-muted fs-5">Seleccione el módulo al que desea acceder</p>
        </div>
    </div>

    <div class="row g-4 justify-content-center">
        <!-- Módulo Personas -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0 hover-card">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <div class="mb-3">
                        <!-- Icono SVG de Bootstrap -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" class="bi bi-people text-primary" viewBox="0 0 16 16">
                            <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1h8Zm-7.978-1A.261.261 0 0 1 7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002a.274.274 0 0 1-.014.002H7.022ZM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0ZM6.936 9.28a5.88 5.88 0 0 0-1.23-.88C4.69 8.18 3 8 3 8s-1 0-1 1 1 4 5 4h.096c-.143-.3-.217-.6-.24-.9H1.196c.005-.264.17-1.03.76-1.72C2.508 9.629 3.478 9 5 9c.474 0 .91.1 1.29.281.04.1.082.203.126.307.112.261.24.526.42.748l-.053-.053Z"/>
                        </svg>
                    </div>
                    <h4 class="card-title fw-bold text-dark mb-3">Personas</h4>
                    <p class="card-text text-muted flex-grow-1">Gestión completa de usuarios, clientes y personal en la base de datos.</p>
                    <a href="PersonaServlet?action=list" class="btn btn-primary w-100 py-2 fw-semibold mt-3">Ingresar</a>
                </div>
            </div>
        </div>

        <!-- Módulo Habitaciones -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0 hover-card">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <div class="mb-3">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" class="bi bi-door-open text-success" viewBox="0 0 16 16">
                            <path d="M8.5 10c-.276 0-.5-.448-.5-1s.224-1 .5-1 .5.448.5 1-.224 1-.5 1z"/>
                            <path d="M10.828.122A.5.5 0 0 1 11 .5V15h1.5a.5.5 0 0 1 0 1h-13a.5.5 0 0 1 0-1H1V.5a.5.5 0 0 1 .432-.499L7.988.07a.5.5 0 0 1 .158.005l2.682.047zM2 15h9V1.088L2 1.54V15z"/>
                        </svg>
                    </div>
                    <h4 class="card-title fw-bold text-dark mb-3">Habitaciones</h4>
                    <p class="card-text text-muted flex-grow-1">Configuración de disponibilidad, tipos de cuartos y mantenimiento.</p>
                    <a href="HabitacionServlet?action=list" class="btn btn-success w-100 py-2 fw-semibold mt-3">Ingresar</a>
                </div>
            </div>
        </div>

        <!-- Módulo Reservas -->
        <div class="col-md-4">
            <div class="card h-100 shadow-sm border-0 hover-card">
                <div class="card-body text-center p-4 d-flex flex-column">
                    <div class="mb-3">
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" class="bi bi-calendar-check text-warning" viewBox="0 0 16 16">
                            <path d="M10.854 7.146a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 1 1 .708-.708L7.5 9.793l2.646-2.647a.5.5 0 0 1 .708 0z"/>
                            <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5zM1 4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4H1z"/>
                        </svg>
                    </div>
                    <h4 class="card-title fw-bold text-dark mb-3">Reservas</h4>
                    <p class="card-text text-muted flex-grow-1">Control de asignaciones, turnos y panel de fechas ocupadas.</p>
                    <a href="ReservaServlet?action=list" class="btn btn-warning w-100 py-2 fw-semibold text-dark mt-3">Ingresar</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>