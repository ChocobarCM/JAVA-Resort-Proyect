<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.LinkedList, java.util.Locale, java.time.LocalDate, java.time.format.DateTimeFormatter" %>
<%@ page import="entities.Habitacion" %>
<%@ page import="entities.Tipo_Habitacion" %>
<%@ page import="entities.Reserva" %>
<%@ page import="logic.Reservas" %>
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

    private static String badge(String estadoVisible) {
        if ("Próxima".equals(estadoVisible)) return "bg-primary";
        if ("En curso".equals(estadoVisible)) return "bg-success";
        if ("Finalizada".equals(estadoVisible)) return "bg-dark";
        return "bg-secondary"; // Cancelada
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis Reservas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    String ok = (String) request.getAttribute("ok");
    String error = (String) request.getAttribute("error");
    LinkedList<Reserva> reservas = (LinkedList<Reserva>) request.getAttribute("reservas");
    LocalDate hoy = LocalDate.now();
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-0">Mis Reservas</h2>
            <p class="text-muted mb-0">Consulte el estado de sus reservas o cancele las próximas</p>
        </div>
        <div>
            <a href="ReservaServlet" class="btn btn-success me-2">Nueva reserva</a>
            <a href="index.jsp" class="btn btn-outline-secondary">Volver al Menú</a>
        </div>
    </div>

    <% if (ok != null) { %>
        <div class="alert alert-success" role="alert"><%= esc(ok) %></div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert alert-danger" role="alert"><%= esc(error) %></div>
    <% } %>

    <% if (reservas != null) { %>
    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <% if (reservas.isEmpty()) { %>
                <div class="text-center text-muted py-5">
                    Todavía no realizó ninguna reserva.
                    <div class="mt-3"><a href="ReservaServlet" class="btn btn-primary">Reservar ahora</a></div>
                </div>
            <% } else { %>
                <div class="table-responsive">
                    <table class="table table-striped table-hover mb-0 align-middle">
                        <thead class="table-secondary">
                            <tr>
                                <th>N°</th>
                                <th>Habitación</th>
                                <th>Entrada</th>
                                <th>Salida</th>
                                <th>Noches</th>
                                <th>Total</th>
                                <th>Estado</th>
                                <th class="text-end">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Reserva r : reservas) {
                                    Habitacion hab = r.getHabitacion();
                                    Tipo_Habitacion th = hab.getTipo_Habitacion();
                                    String estado = Reservas.estadoVisible(r, hoy);
                            %>
                                <tr>
                                    <td class="fw-bold"><%= r.getIdReserva() %></td>
                                    <td>
                                        <%= esc(hab.getNum()) %> &middot; Piso <%= hab.getPiso() %>
                                        <div class="small text-muted"><%= esc(th.getNomb()) %> (<%= th.getCapacidad() %> pax)</div>
                                    </td>
                                    <td><%= r.getFechaEntrada().format(FECHA) %></td>
                                    <td><%= r.getFechaSalida().format(FECHA) %></td>
                                    <td><%= Reservas.noches(r.getFechaEntrada(), r.getFechaSalida()) %></td>
                                    <td class="fw-semibold"><%= money(r.getPrecioTotal()) %></td>
                                    <td><span class="badge <%= badge(estado) %>"><%= estado %></span></td>
                                    <td class="text-end">
                                        <% if (Reservas.esCancelable(r, hoy)) { %>
                                            <form action="MisReservasServlet" method="POST" class="d-inline"
                                                  onsubmit="return confirm('¿Seguro que desea cancelar esta reserva?');">
                                                <input type="hidden" name="action" value="cancelar">
                                                <input type="hidden" name="idReserva" value="<%= r.getIdReserva() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">Cancelar</button>
                                            </form>
                                        <% } %>
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
</body>
</html>
