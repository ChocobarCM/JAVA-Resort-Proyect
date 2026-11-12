<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList, java.util.Locale, java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="entities.Habitacion" %>
<%@ page import="entities.Tipo_Habitacion" %>
<%@ page import="entities.Reserva" %>
<%!
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Escapa HTML para no inyectar texto cargado por el usuario directamente en la página
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String money(double v) {
        return "$" + String.format(Locale.forLanguageTag("es-AR"), "%,.2f", v);
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reservar</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    String error = (String) request.getAttribute("error");
    Reserva creada = (Reserva) request.getAttribute("reservaCreada");

    String entradaTxt = (String) request.getAttribute("entradaTxt");
    String salidaTxt = (String) request.getAttribute("salidaTxt");
    LocalDate entrada = (LocalDate) request.getAttribute("entrada");
    LocalDate salida = (LocalDate) request.getAttribute("salida");
    Long noches = (Long) request.getAttribute("noches");
    LinkedList<Habitacion> disponibles = (LinkedList<Habitacion>) request.getAttribute("disponibles");

    String hoy = LocalDate.now().toString();
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-0">Reservar</h2>
            <p class="text-muted mb-0">Elija sus fechas y vea las habitaciones disponibles</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-secondary">Volver al Menú</a>
    </div>

    <% if (creada != null) { %>
        <div class="alert alert-success" role="alert">
            <h5 class="alert-heading mb-2">¡Reserva confirmada!</h5>
            <div>Reserva N° <strong><%= creada.getIdReserva() %></strong> &middot;
                Habitación <strong><%= esc(creada.getHabitacion().getNum()) %></strong>
                (<%= esc(creada.getHabitacion().getTipo_Habitacion().getNomb()) %>)</div>
            <div>Del <strong><%= creada.getFechaEntrada().format(FECHA) %></strong>
                al <strong><%= creada.getFechaSalida().format(FECHA) %></strong>
                &middot; Total: <strong><%= money(creada.getPrecioTotal()) %></strong></div>
        </div>
    <% } %>

    <% if (error != null) { %>
        <div class="alert alert-danger" role="alert"><%= esc(error) %></div>
    <% } %>

    <!-- Búsqueda por fechas -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body p-4">
            <form action="ReservaServlet" method="GET" class="row g-3 align-items-end">
                <input type="hidden" name="action" value="buscar">
                <div class="col-md-4">
                    <label class="form-label fw-semibold" for="entrada">Fecha de entrada</label>
                    <input type="date" class="form-control" id="entrada" name="entrada" required
                           min="<%= hoy %>" value="<%= esc(entradaTxt) %>">
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold" for="salida">Fecha de salida</label>
                    <input type="date" class="form-control" id="salida" name="salida" required
                           min="<%= hoy %>" value="<%= esc(salidaTxt) %>">
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">Buscar disponibilidad</button>
                </div>
            </form>
        </div>
    </div>

    <% if (disponibles != null) { %>
        <div class="card shadow-sm border-0">
            <div class="card-header bg-dark text-white py-3">
                <h5 class="card-title mb-0">
                    Habitaciones disponibles del <%= entrada.format(FECHA) %> al <%= salida.format(FECHA) %>
                    (<%= noches %> <%= noches == 1 ? "noche" : "noches" %>)
                </h5>
            </div>
            <div class="card-body p-0">
                <% if (disponibles.isEmpty()) { %>
                    <div class="text-center text-muted py-4">
                        No hay habitaciones disponibles para esas fechas. Pruebe con otras fechas.
                    </div>
                <% } else { %>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover mb-0 align-middle">
                            <thead class="table-secondary">
                                <tr>
                                    <th>Habitación</th>
                                    <th>Piso</th>
                                    <th>Tipo</th>
                                    <th>Capacidad</th>
                                    <th>Precio/noche</th>
                                    <th>Total</th>
                                    <th class="text-end"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (Habitacion hab : disponibles) {
                                        Tipo_Habitacion th = hab.getTipo_Habitacion();
                                %>
                                    <tr>
                                        <td class="fw-bold"><%= esc(hab.getNum()) %></td>
                                        <td><%= hab.getPiso() %></td>
                                        <td>
                                            <%= esc(th.getNomb()) %>
                                            <% if (th.getDescripcion() != null && !th.getDescripcion().isEmpty()) { %>
                                                <div class="small text-muted"><%= esc(th.getDescripcion()) %></div>
                                            <% } %>
                                        </td>
                                        <td><%= th.getCapacidad() %> pax</td>
                                        <td><%= money(th.getPrecioNoche()) %></td>
                                        <td class="fw-semibold"><%= money(th.getPrecioNoche() * noches) %></td>
                                        <td class="text-end">
                                            <a class="btn btn-sm btn-success"
                                               href="ReservaServlet?action=confirmar&idHab=<%= hab.getIdHab() %>&entrada=<%= entrada %>&salida=<%= salida %>">Reservar</a>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>
        </div>
    <% } %>
</div>

<script>
    // La salida no puede ser anterior (ni igual) a la entrada: se ajusta el mínimo del segundo campo
    (function () {
        var entrada = document.getElementById('entrada');
        var salida = document.getElementById('salida');
        entrada.addEventListener('change', function () {
            if (!entrada.value) return;
            var d = new Date(entrada.value + 'T00:00:00');
            d.setDate(d.getDate() + 1);
            var mm = ('0' + (d.getMonth() + 1)).slice(-2);
            var dd = ('0' + d.getDate()).slice(-2);
            salida.min = d.getFullYear() + '-' + mm + '-' + dd;
            if (salida.value && salida.value < salida.min) salida.value = '';
        });
    })();
</script>
</body>
</html>
