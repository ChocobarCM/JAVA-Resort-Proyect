<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="Entities.Persona" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Personas</title>
    <!-- CDN Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container py-5">
    <div class="row mb-4">
        <div class="col">
            <h2 class="text-primary fw-bold">Administración de Personas</h2>
            <p class="text-muted">ABM básico conectado a base de datos</p>
        </div>
    </div>

    <div class="row">
        <!-- Columna Izquierda: Formulario de Alta -->
        <div class="col-lg-4 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h5 class="card-title mb-0">Nueva Persona</h5>
                </div>
                <div class="card-body p-4">
                    <form action="PersonaServlet" method="POST">
                        <input type="hidden" name="action" value="create">
                        
                        <div class="mb-3">
                            <label for="dni" class="form-label fw-semibold">DNI</label>
                            <input type="number" class="form-control" id="dni" name="dni" required placeholder="Ej: 12345678">
                        </div>
                        <div class="mb-3">
                            <label for="nombre" class="form-label fw-semibold">Nombre</label>
                            <input type="text" class="form-control" id="nombre" name="nombre" required placeholder="Ej: Juan">
                        </div>
                        <div class="mb-3">
                            <label for="apellido" class="form-label fw-semibold">Apellido</label>
                            <input type="text" class="form-control" id="apellido" name="apellido" required placeholder="Ej: Perez">
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label fw-semibold">Email</label>
                            <input type="email" class="form-control" id="email" name="email" required placeholder="juanperez@mail.com">
                        </div>
                        <button type="submit" class="btn btn-success w-100 py-2 mt-2 fw-semibold">Guardar Persona</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Columna Derecha: Tabla de Listado -->
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-dark text-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Listado de Registros</h5>
                    <a href="PersonaServlet?action=list" class="btn btn-sm btn-outline-light">Refrescar</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover mb-0 align-middle">
                            <thead class="table-secondary">
                                <tr>
                                    <th>ID</th>
                                    <th>DNI</th>
                                    <th>Nombre Completo</th>
                                    <th>Email</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    LinkedList<Persona> lista = (LinkedList<Persona>) request.getAttribute("listaPersonas");
                                    if (lista != null && !lista.isEmpty()) {
                                        for (Persona per : lista) {
                                %>
                                    <tr>
                                        <td class="fw-bold"><%= per.getIdPersona() %></td>
                                        <td><%= per.getDni() %></td>
                                        <td><%= per.getNombre() %> <%= per.getApellido() %></td>
                                        <td><%= per.getEmail() %></td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-4">No hay personas registradas o se ingresó sin pasar por el Servlet.</td>
                                    </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
    }
}
