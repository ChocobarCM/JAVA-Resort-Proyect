<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="Entities.Persona" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Personas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    // Verificamos si el Servlet nos mandó una persona para editar
    Persona pEdit = (Persona) request.getAttribute("personaEditar");
    boolean editando = (pEdit != null);
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-0">Administración de Personas</h2>
            <p class="text-muted mb-0">ABMC completo conectado a base de datos</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-secondary">Volver al Menú</a>
    </div>

    <div class="row">
        <!-- Columna Izquierda: Formulario Dinámico (Alta / Modificación) -->
        <div class="col-lg-4 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-header <%= editando ? "bg-warning text-dark" : "bg-primary text-white" %> py-3">
                    <h5 class="card-title mb-0"><%= editando ? "Modificar Persona (ID: " + pEdit.getIdPersona() + ")" : "Nueva Persona" %></h5>
                </div>
                <div class="card-body p-4">
                    <form action="PersonaServlet" method="POST">
                        <!-- Si estamos editando, mandamos action="update", sino action="create" -->
                        <input type="hidden" name="action" value="<%= editando ? "update" : "create" %>">
                        
                        <% if (editando) { %>
                            <!-- Si editamos, necesitamos mandar el ID oculto para el WHERE de SQL -->
                            <input type="hidden" name="idPersona" value="<%= pEdit.getIdPersona() %>">
                        <% } %>
                        
                        <div class="mb-3">
                            <label class="form-label fw-semibold">DNI</label>
                            <input type="number" class="form-control" name="dni" required 
                                   value="<%= editando ? pEdit.getDni() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Nombre</label>
                            <input type="text" class="form-control" name="nombre" required 
                                   value="<%= editando ? pEdit.getNombre() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Apellido</label>
                            <input type="text" class="form-control" name="apellido" required 
                                   value="<%= editando ? pEdit.getApellido() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" class="form-control" name="email" required 
                                   value="<%= editando ? pEdit.getEmail() : "" %>">
                        </div>
                        
                        <button type="submit" class="btn <%= editando ? "btn-warning" : "btn-success" %> w-100 py-2 mt-2 fw-semibold">
                            <%= editando ? "Guardar Cambios" : "Guardar Persona" %>
                        </button>
                        
                        <% if (editando) { %>
                            <a href="PersonaServlet?action=list" class="btn btn-link w-100 mt-2 text-secondary">Cancelar edición</a>
                        <% } %>
                    </form>
                </div>
            </div>
        </div>

        <!-- Columna Derecha: Tabla de Listado y Baja -->
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
                                    <th class="text-end">Acciones</th>
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
                                        <td class="text-end">
                                            <!-- Botón Editar: Viaja por GET al Servlet pidiendo cargar los datos -->
                                            <a href="PersonaServlet?action=edit&id=<%= per.getIdPersona() %>" class="btn btn-sm btn-outline-primary me-1">Editar</a>
                                            
                                            <!-- Botón Eliminar: Mini-formulario que viaja por POST para borrar -->
                                            <form action="PersonaServlet" method="POST" class="d-inline" onsubmit="return confirm('¿Seguro que deseas eliminar a esta persona?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="idPersona" value="<%= per.getIdPersona() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Borrar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">No hay personas registradas.</td>
                                    </tr>
                                <%  } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>