<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="entities.Habitacion" %>
<%@ page import="entities.Tipo_Habitacion" %>
<%!
    // Escapa HTML para no inyectar texto cargado por el usuario directamente en la página
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    // Estados posibles de una habitación (columna habitacion.estado)
    private static final String[] ESTADOS = {"DISPONIBLE", "OCUPADA", "MANTENIMIENTO"};

    private static String badgeEstado(String estado) {
        if ("DISPONIBLE".equals(estado)) return "bg-success";
        if ("OCUPADA".equals(estado)) return "bg-danger";
        if ("MANTENIMIENTO".equals(estado)) return "bg-warning text-dark";
        return "bg-secondary";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Habitaciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    // Verificamos si el Servlet nos mandó una habitación para editar
    Habitacion hEdit = (Habitacion) request.getAttribute("habitacionEditar");
    boolean editando = (hEdit != null);

    LinkedList<Tipo_Habitacion> tipos = (LinkedList<Tipo_Habitacion>) request.getAttribute("listaTipos");
    boolean hayTipos = (tipos != null && !tipos.isEmpty());
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-0">Gestión de Habitaciones</h2>
            <p class="text-muted mb-0">ABMC de habitaciones y su tipo</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-secondary">Volver al Menú</a>
    </div>

    <div class="row">
        <!-- Columna Izquierda: Formulario Dinámico (Alta / Modificación) -->
        <div class="col-lg-4 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-header <%= editando ? "bg-warning text-dark" : "bg-success text-white" %> py-3">
                    <h5 class="card-title mb-0"><%= editando ? "Modificar Habitación (ID: " + hEdit.getIdHab() + ")" : "Nueva Habitación" %></h5>
                </div>
                <div class="card-body p-4">

                    <% if (!hayTipos) { %>
                        <div class="alert alert-warning py-2" role="alert">
                            No hay tipos de habitación cargados. Primero cree uno en
                            <a href="TipoHabitacionServlet?action=list" class="alert-link">Tipos de Cuartos</a>.
                        </div>
                    <% } %>

                    <form action="HabitacionServlet" method="POST">
                        <!-- Si estamos editando, mandamos action="update", sino action="create" -->
                        <input type="hidden" name="action" value="<%= editando ? "update" : "create" %>">

                        <% if (editando) { %>
                            <!-- Si editamos, necesitamos mandar el ID oculto para el WHERE de SQL -->
                            <input type="hidden" name="idHab" value="<%= hEdit.getIdHab() %>">
                        <% } %>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Número</label>
                            <input type="text" class="form-control" name="num" required maxlength="20"
                                   value="<%= editando ? esc(hEdit.getNum()) : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Piso</label>
                            <input type="number" class="form-control" name="piso" required min="0"
                                   value="<%= editando ? hEdit.getPiso() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Tipo de Habitación</label>
                            <select class="form-select" name="idTipoHab" required <%= hayTipos ? "" : "disabled" %>>
                                <option value="" disabled <%= editando ? "" : "selected" %>>Seleccione un tipo...</option>
                                <%
                                    if (hayTipos) {
                                        for (Tipo_Habitacion t : tipos) {
                                            boolean sel = editando && hEdit.getTipo_Habitacion() != null
                                                    && hEdit.getTipo_Habitacion().getIdTipoHab() == t.getIdTipoHab();
                                %>
                                    <option value="<%= t.getIdTipoHab() %>" <%= sel ? "selected" : "" %>>
                                        <%= esc(t.getNomb()) %> (<%= t.getCapacidad() %> pax - $<%= t.getPrecioNoche() %>)
                                    </option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Estado</label>
                            <select class="form-select" name="estado" required>
                                <%
                                    for (String est : ESTADOS) {
                                        boolean selEst = editando ? est.equals(hEdit.getEstado()) : "DISPONIBLE".equals(est);
                                %>
                                    <option value="<%= est %>" <%= selEst ? "selected" : "" %>><%= est %></option>
                                <% } %>
                            </select>
                        </div>

                        <button type="submit" class="btn <%= editando ? "btn-warning" : "btn-success" %> w-100 py-2 mt-2 fw-semibold"
                                <%= hayTipos ? "" : "disabled" %>>
                            <%= editando ? "Guardar Cambios" : "Guardar Habitación" %>
                        </button>

                        <% if (editando) { %>
                            <a href="HabitacionServlet?action=list" class="btn btn-link w-100 mt-2 text-secondary">Cancelar edición</a>
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
                    <a href="HabitacionServlet?action=list" class="btn btn-sm btn-outline-light">Refrescar</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover mb-0 align-middle">
                            <thead class="table-secondary">
                                <tr>
                                    <th>ID</th>
                                    <th>Número</th>
                                    <th>Piso</th>
                                    <th>Tipo</th>
                                    <th>Capacidad</th>
                                    <th>Precio/noche</th>
                                    <th>Estado</th>
                                    <th class="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    LinkedList<Habitacion> lista = (LinkedList<Habitacion>) request.getAttribute("listaHabitaciones");
                                    if (lista != null && !lista.isEmpty()) {
                                        for (Habitacion hab : lista) {
                                            Tipo_Habitacion th = hab.getTipo_Habitacion();
                                %>
                                    <tr>
                                        <td class="fw-bold"><%= hab.getIdHab() %></td>
                                        <td><%= esc(hab.getNum()) %></td>
                                        <td><%= hab.getPiso() %></td>
                                        <td><%= th != null ? esc(th.getNomb()) : "-" %></td>
                                        <td><%= th != null ? th.getCapacidad() + " pax" : "-" %></td>
                                        <td><%= th != null ? "$" + th.getPrecioNoche() : "-" %></td>
                                        <td><span class="badge <%= badgeEstado(hab.getEstado()) %>"><%= esc(hab.getEstado()) %></span></td>
                                        <td class="text-end">
                                            <!-- Botón Editar: Viaja por GET al Servlet pidiendo cargar los datos -->
                                            <a href="HabitacionServlet?action=edit&id=<%= hab.getIdHab() %>" class="btn btn-sm btn-outline-primary me-1">Editar</a>

                                            <!-- Botón Eliminar: Mini-formulario que viaja por POST para borrar -->
                                            <form action="HabitacionServlet" method="POST" class="d-inline" onsubmit="return confirm('¿Seguro que deseas eliminar esta habitación?');">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="idHab" value="<%= hab.getIdHab() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Borrar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    } else {
                                %>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-4">No hay habitaciones registradas.</td>
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
