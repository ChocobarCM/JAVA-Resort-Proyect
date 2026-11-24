<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList" %>
<%@ page import="entities.Habitacion" %>
<%@ page import="entities.Tipo_Habitacion" %>
<%@ page import="entities.ImagenHabitacion" %>
<%@ page import="logic.GestorImagenes" %>
<%!
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static final String[] ESTADOS = {"DISPONIBLE", "OCUPADA", "MANTENIMIENTO"};

    private static String badgeEstado(String estado) {
        if ("DISPONIBLE".equals(estado)) return "bg-success";
        if ("OCUPADA".equals(estado)) return "bg-danger";
        if ("MANTENIMIENTO".equals(estado)) return "bg-warning text-dark";
        return "bg-secondary";
    }
%>
<!DOCTYPE html>
<html lang="es" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Habitaciones - Resort Grand Line</title>
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
    Habitacion hEdit = (Habitacion) request.getAttribute("habitacionEditar");
    boolean editando = (hEdit != null);

    LinkedList<Tipo_Habitacion> tipos = (LinkedList<Tipo_Habitacion>) request.getAttribute("listaTipos");
    boolean hayTipos = (tipos != null && !tipos.isEmpty());

    // cuántas imágenes más admite esta habitación (máximo por habitación - las que ya tiene)
    int cupoImagenes = GestorImagenes.MAX_IMAGENES_POR_HABITACION - (editando ? hEdit.getImagenes().size() : 0);
    long maxMb = GestorImagenes.TAMANIO_MAX / (1024 * 1024);

    LinkedList<String> avisos = (LinkedList<String>) request.getAttribute("avisos");
%>

<div class="container py-5">
    <% if (avisos != null && !avisos.isEmpty()) { %>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <strong>Atención:</strong>
            <ul class="mb-0">
                <% for (String aviso : avisos) { %>
                    <li><%= esc(aviso) %></li>
                <% } %>
            </ul>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    <% } %>

    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-0">Gestión de Camarotes y Suites</h2>
            <p class="text-op-muted mb-0">ABMC de habitaciones y su categoría</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-op">Volver al Menú</a>
    </div>

    <div class="row">
        <div class="col-lg-4 mb-4">
            <div class="card card-op-static">
                <div class="card-header py-3">
                    <h5 class="card-title mb-0"><%= editando ? "Modificar Habitación (ID: " + hEdit.getIdHab() + ")" : "Nueva Habitación" %></h5>
                </div>
                <div class="card-body p-4">

                    <% if (!hayTipos) { %>
                        <div class="alert alert-warning py-2" role="alert">
                            No hay tipos de habitación cargados. Primero cree uno en
                            <a href="TipoHabitacionServlet?action=list" class="alert-link">Categorías de Camarote</a>.
                        </div>
                    <% } %>

                    <form action="HabitacionServlet" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="<%= editando ? "update" : "create" %>">

                        <% if (editando) { %>
                            <input type="hidden" name="idHab" value="<%= hEdit.getIdHab() %>">
                        <% } %>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Número</label>
                            <input type="text" class="form-control form-control-op" name="num" required maxlength="20"
                                   value="<%= editando ? esc(hEdit.getNum()) : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Piso / Cubierta</label>
                            <input type="number" class="form-control form-control-op" name="piso" required min="0"
                                   value="<%= editando ? hEdit.getPiso() : "" %>">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Tipo de Habitación</label>
                            <select class="form-select form-select-op" name="idTipoHab" required <%= hayTipos ? "" : "disabled" %>>
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
                            <select class="form-select form-select-op" name="estado" required>
                                <%
                                    for (String est : ESTADOS) {
                                        boolean selEst = editando ? est.equals(hEdit.getEstado()) : "DISPONIBLE".equals(est);
                                %>
                                    <option value="<%= est %>" <%= selEst ? "selected" : "" %>><%= est %></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold" for="imagenes">Imágenes</label>
                            <input type="file" class="form-control form-control-op" id="imagenes" name="imagenes" multiple
                                   accept="image/jpeg,image/png,image/gif,image/webp" <%= cupoImagenes <= 0 ? "disabled" : "" %>>
                            <div class="form-text text-op-muted">
                                <% if (cupoImagenes > 0) { %>
                                    JPG, PNG, GIF o WEBP, hasta <%= maxMb %> MB cada una. Puede agregar <%= cupoImagenes %> más.
                                <% } else { %>
                                    Esta habitación ya tiene el máximo de <%= GestorImagenes.MAX_IMAGENES_POR_HABITACION %> imágenes.
                                <% } %>
                            </div>
                            <div id="errorImagenes" class="text-danger small mt-1"></div>
                            <div id="previewImagenes" class="d-flex flex-wrap gap-2 mt-2"></div>
                        </div>

                        <button type="submit" class="btn btn-op-gold w-100 py-2 mt-2" <%= hayTipos ? "" : "disabled" %>>
                            <%= editando ? "Guardar Cambios" : "Guardar Habitación" %>
                        </button>

                        <% if (editando) { %>
                            <a href="HabitacionServlet?action=list" class="btn btn-outline-light w-100 mt-2">Cancelar edición</a>
                        <% } %>
                    </form>

                    <% if (editando) {
                           LinkedList<ImagenHabitacion> imgs = hEdit.getImagenes();
                    %>
                        <hr class="my-4">
                        <h6 class="fw-semibold">Imágenes actuales (<%= imgs.size() %>/<%= GestorImagenes.MAX_IMAGENES_POR_HABITACION %>)</h6>
                        <% if (imgs.isEmpty()) { %>
                            <p class="text-op-muted small mb-0">Esta habitación todavía no tiene imágenes.</p>
                        <% } else { %>
                            <div class="row g-2">
                                <% for (ImagenHabitacion im : imgs) { %>
                                    <div class="col-6">
                                        <div class="position-relative">
                                            <a href="ImagenServlet?id=<%= im.getIdImagen() %>" target="_blank">
                                                <img src="ImagenServlet?id=<%= im.getIdImagen() %>" loading="lazy"
                                                     alt="Imagen de la habitación <%= esc(hEdit.getNum()) %>"
                                                     class="w-100 rounded border" style="aspect-ratio: 4/3; object-fit: cover;">
                                            </a>
                                            <form action="HabitacionServlet" method="POST" class="position-absolute top-0 end-0 m-1"
                                                  onsubmit="return confirm('¿Seguro que deseas eliminar esta imagen?');">
                                                <input type="hidden" name="action" value="deleteImage">
                                                <input type="hidden" name="idHab" value="<%= hEdit.getIdHab() %>">
                                                <input type="hidden" name="idImagen" value="<%= im.getIdImagen() %>">
                                                <button type="submit" class="btn btn-sm btn-danger py-0 px-2" title="Eliminar imagen">&times;</button>
                                            </form>
                                        </div>
                                    </div>
                                <% } %>
                            </div>
                        <% } %>
                    <% } %>
                </div>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="card card-op-static">
                <div class="card-header py-3 d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">Listado de Registros</h5>
                    <a href="HabitacionServlet?action=list" class="btn btn-sm btn-outline-op">Refrescar</a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-op table-striped table-hover mb-0 align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Imagen</th>
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
                                        <td class="fw-bold text-op-gold"><%= hab.getIdHab() %></td>
                                        <td>
                                            <% ImagenHabitacion portada = hab.getPortada(); %>
                                            <% if (portada != null) { %>
                                                <a href="ImagenServlet?id=<%= portada.getIdImagen() %>" target="_blank">
                                                    <img src="ImagenServlet?id=<%= portada.getIdImagen() %>" loading="lazy"
                                                         alt="Imagen de la habitación <%= esc(hab.getNum()) %>"
                                                         class="rounded border" style="width: 64px; height: 48px; object-fit: cover;">
                                                </a>
                                                <% if (hab.getImagenes().size() > 1) { %>
                                                    <span class="badge bg-secondary ms-1">+<%= hab.getImagenes().size() - 1 %></span>
                                                <% } %>
                                            <% } else { %>
                                                <span class="text-op-muted small">Sin imagen</span>
                                            <% } %>
                                        </td>
                                        <td><%= esc(hab.getNum()) %></td>
                                        <td><%= hab.getPiso() %></td>
                                        <td><%= th != null ? esc(th.getNomb()) : "-" %></td>
                                        <td><%= th != null ? th.getCapacidad() + " pax" : "-" %></td>
                                        <td><%= th != null ? "$" + th.getPrecioNoche() : "-" %></td>
                                        <td><span class="badge <%= badgeEstado(hab.getEstado()) %>"><%= esc(hab.getEstado()) %></span></td>
                                        <td class="text-end">
                                            <a href="HabitacionServlet?action=edit&id=<%= hab.getIdHab() %>" class="btn btn-sm btn-outline-op me-1">Editar</a>
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
                                        <td colspan="9" class="text-center text-op-muted py-4">No hay habitaciones registradas.</td>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Vista previa y validación rápida en el navegador. La validación REAL la hace el servidor (GestorImagenes).
    (function () {
        var input = document.getElementById('imagenes');
        var preview = document.getElementById('previewImagenes');
        var error = document.getElementById('errorImagenes');
        var MAX_BYTES = <%= GestorImagenes.TAMANIO_MAX %>;
        var CUPO = <%= cupoImagenes %>;
        var TIPOS = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

        input.addEventListener('change', function () {
            preview.innerHTML = '';
            error.textContent = '';
            var archivos = Array.prototype.slice.call(input.files);
            var problema = '';

            if (archivos.length > CUPO) {
                problema = 'Solo se pueden agregar ' + CUPO + ' imagen(es) más a esta habitación.';
            }
            archivos.forEach(function (f) {
                if (TIPOS.indexOf(f.type) === -1) {
                    problema = '"' + f.name + '" no tiene un formato permitido (JPG, PNG, GIF o WEBP).';
                } else if (f.size > MAX_BYTES) {
                    problema = '"' + f.name + '" supera los <%= maxMb %> MB.';
                }
            });

            if (problema) {
                error.textContent = problema;
                input.value = '';
                return;
            }

            archivos.forEach(function (f) {
                var img = document.createElement('img');
                img.src = URL.createObjectURL(f);
                img.alt = f.name;
                img.className = 'rounded border';
                img.style.cssText = 'width: 72px; height: 54px; object-fit: cover;';
                preview.appendChild(img);
            });
        });
    })();
</script>
</body>
</html>