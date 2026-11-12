<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="entities.Persona" %>
<!DOCTYPE html>
<html lang="es" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Personas - Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/onepiece.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-op">
    <div class="container">
        <a class="navbar-brand fw-bold text-op-gold" href="index.jsp">RESORT GRAND LINE</a>
    </div>
</nav>

<%
    Persona pEdit = (Persona) request.getAttribute("personaEditar");
    boolean editando = (pEdit != null);
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-0">Administración de Personas</h2>
            <p class="text-op-muted mb-0">ABMC completo conectado a base de datos</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-op">Volver al Menú</a>
    </div>

    <div class="row">
        <div class="col-lg-4 mb-4">
            <div class="card card-op-static">
                <div class="card-header py-3">
                    <h5 class="card-title mb-0"><%= editando ? "Modificar Persona (ID: " + pEdit.getIdPersona() + ")" : "Nueva Persona" %></h5>
                </div>
                <div class="card-body p-4">
                    <form action="PersonaServlet" method="POST">
                        <input type="hidden" name="action" value="<%= editando ? "update" : "create" %>">
                        
                        <% if (editando) { %>
                            <input type="hidden" name="idPersona" value="<%= pEdit.getIdPersona() %>">
                        <% } %>
                        
                        <div class="mb-3">
                            <label class="form-label fw-semibold">DNI</label>
                            <input type="number" class="form-control form-control-op" name="dni" required 
                                   value="<%= editando ? pEdit.getDni() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Nombre</label>
                            <input type="text" class="form-control form-control-op" name="nombre" required 
                                   value="<%= editando ? pEdit.getNombre() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Apellido</label>
                            <input type="text" class="form-control form-control-op" name="apellido" required 
                                   value="<%= editando ? pEdit.getApellido() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" class="form-control form-control-op" name="email" required 
                                   value="<%= editando ? pEdit.getEmail() : "" %>">
                        </div>
                        
                        <button type="submit" class="btn btn-op-gold w-100 py-2 mt-2">
                            <%= editando ? "Guardar Cambios" : "Guardar Persona" %>
                        </button>
                        
                        <% if (editando) { %>
                            <a href="PersonaServlet?action=list" class="btn btn-outline-light w-100 mt-2">Cancelar edición</a>
                        <% } %>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-8">
            <div class="card card-op-static">
                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Listado de Registros</h5>
                    <a href="PersonaServlet?action=list" class="btn btn-sm btn-outline-op">Refrescar</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-op table-striped table-hover mb-0 align-middle">
                            <thead>
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
                                        <td class="fw-bold text-op-gold"><%= per.getIdPersona() %></td>
                                        <td><%= per.getDni() %></td>
                                        <td><%= per.getNombre() %> <%= per.getApellido() %></td>
                                        <td><%= per.getEmail() %></td>
                                        <td class="text-end">
                                            <a href="PersonaServlet?action=edit&id=<%= per.getIdPersona() %>" class="btn btn-sm btn-outline-op me-1">Editar</a>
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
                                        <td colspan="5" class="text-center text-op-muted py-4">No hay personas registradas.</td>
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