<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="entities.Tipo_Habitacion" %>
<!DOCTYPE html>
<html lang="es" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Categorías de Camarote - Resort Grand Line</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/onepiece.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-op">
    <div class="container">
        <a class="navbar-brand fw-bold text-op-gold" href="<%= request.getContextPath() %>/index.jsp">RESORT GRAND LINE</a>
    </div>
</nav>

<%
    Tipo_Habitacion thEdit = (Tipo_Habitacion) request.getAttribute("tipoHabitacionEditar");
    boolean editando = (thEdit != null);
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-0">Categorías de Camarote</h2>
            <p class="text-op-muted mb-0">Configuración de capacidades y precios en Berries</p>
        </div>
        <a href="<%= request.getContextPath() %>/index.jsp" class="btn btn-outline-op">Volver al Menú</a>
    </div>

    <div class="row">
        <div class="col-lg-4 mb-4">
            <div class="card card-op-static">
                <div class="card-header py-3">
                    <h5 class="card-title mb-0"><%= editando ? "Modificar Categoría (ID: " + thEdit.getIdTipoHab() + ")" : "Nueva Categoría" %></h5>
                </div>
                <div class="card-body p-4">
                    <form action="TipoHabitacionServlet" method="POST">
                        <input type="hidden" name="action" value="<%= editando ? "update" : "create" %>">
                        
                        <% if (editando) { %>
                            <input type="hidden" name="idTipoHab" value="<%= thEdit.getIdTipoHab() %>">
                        <% } %>
                        
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Nombre</label>
                            <input type="text" class="form-control form-control-op" name="nomb" required 
                                   value="<%= editando ? thEdit.getNomb() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Descripción</label>
                            <input type="text" class="form-control form-control-op" name="descripcion" required 
                                   value="<%= editando ? thEdit.getDescripcion() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Capacidad (Personas)</label>
                            <input type="number" class="form-control form-control-op" name="capacidad" required min="1"
                                   value="<%= editando ? thEdit.getCapacidad() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Precio por Noche ($)</label>
                            <input type="number" step="0.01" class="form-control form-control-op" name="precioNoche" required min="0"
                                   value="<%= editando ? thEdit.getPrecioNoche() : "" %>">
                        </div>
                        
                        <button type="submit" class="btn btn-op-gold w-100 py-2 mt-2">
                            <%= editando ? "Guardar Cambios" : "Guardar Categoría" %>
                        </button>
                        
                        <% if (editando) { %>
                            <a href="TipoHabitacionServlet?action=list" class="btn btn-outline-light w-100 mt-2">Cancelar edición</a>
                        <% } %>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-8">
            <div class="card card-op-static">
                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Listado de Registros</h5>
                    <a href="TipoHabitacionServlet?action=list" class="btn btn-sm btn-outline-op">Refrescar</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-op table-striped table-hover mb-0 align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Descripción</th>
                                    <th>Capacidad</th>
                                    <th>Precio</th>
                                    <th class="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    LinkedList<Tipo_Habitacion> lista = (LinkedList<Tipo_Habitacion>) request.getAttribute("listaTipoHabitacion");
                                    if (lista != null && !lista.isEmpty()) {
                                        for (Tipo_Habitacion th : lista) {
                                %>
                                    <tr>
                                        <td class="fw-bold text-op-gold"><%= th.getIdTipoHab() %></td>
                                        <td><%= th.getNomb() %></td>
                                        <td><%= th.getDescripcion() %></td>
                                        <td><%= th.getCapacidad() %> pax</td>
                                        <td>$<%= th.getPrecioNoche() %></td>
                                        <td class="text-end">
                                            <a href="TipoHabitacionServlet?action=edit&id=<%= th.getIdTipoHab() %>" class="btn btn-sm btn-outline-op me-1">Editar</a>
                                            <form action="TipoHabitacionServlet" method="POST" class="d-inline" onsubmit="return confirm('¿Seguro que deseas eliminar esta categoría?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="idTipoHab" value="<%= th.getIdTipoHab() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Borrar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="6" class="text-center text-op-muted py-4">No hay tipos de habitación registrados.</td>
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