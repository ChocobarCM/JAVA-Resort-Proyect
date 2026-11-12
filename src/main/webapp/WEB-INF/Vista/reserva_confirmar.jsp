<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale, java.time.format.DateTimeFormatter" %>
<%@ page import="entities.Habitacion" %>
<%@ page import="entities.Tipo_Habitacion" %>
<%@ page import="entities.Persona" %>
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
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Confirmar reserva</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<%
    Reserva r = (Reserva) request.getAttribute("reserva");
    Persona p = r.getPersona();
    Habitacion hab = r.getHabitacion();
    Tipo_Habitacion th = hab.getTipo_Habitacion();
    long noches = Reservas.noches(r.getFechaEntrada(), r.getFechaSalida());
%>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="text-primary fw-bold mb-0">Confirmar reserva</h2>
            <p class="text-muted mb-0">Revise los datos antes de confirmar</p>
        </div>
        <a href="index.jsp" class="btn btn-outline-secondary">Volver al Menú</a>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-dark text-white py-3">
                    <h5 class="card-title mb-0">Resumen de la reserva</h5>
                </div>
                <div class="card-body p-4">

                    <h6 class="text-uppercase text-muted fw-bold small">Huésped</h6>
                    <dl class="row mb-4">
                        <dt class="col-sm-4">Nombre y apellido</dt>
                        <dd class="col-sm-8"><%= esc(p.getNombre()) %> <%= esc(p.getApellido()) %></dd>
                        <dt class="col-sm-4">DNI</dt>
                        <dd class="col-sm-8"><%= p.getDni() %></dd>
                        <dt class="col-sm-4">Email</dt>
                        <dd class="col-sm-8"><%= esc(p.getEmail()) %></dd>
                    </dl>

                    <h6 class="text-uppercase text-muted fw-bold small">Habitación</h6>
                    <dl class="row mb-4">
                        <dt class="col-sm-4">Número / Piso</dt>
                        <dd class="col-sm-8"><%= esc(hab.getNum()) %> &middot; Piso <%= hab.getPiso() %></dd>
                        <dt class="col-sm-4">Tipo</dt>
                        <dd class="col-sm-8"><%= esc(th.getNomb()) %> (<%= th.getCapacidad() %> pax)</dd>
                    </dl>

                    <h6 class="text-uppercase text-muted fw-bold small">Estadía</h6>
                    <dl class="row mb-4">
                        <dt class="col-sm-4">Entrada</dt>
                        <dd class="col-sm-8"><%= r.getFechaEntrada().format(FECHA) %></dd>
                        <dt class="col-sm-4">Salida</dt>
                        <dd class="col-sm-8"><%= r.getFechaSalida().format(FECHA) %></dd>
                        <dt class="col-sm-4">Noches</dt>
                        <dd class="col-sm-8"><%= noches %></dd>
                        <dt class="col-sm-4">Precio por noche</dt>
                        <dd class="col-sm-8"><%= money(th.getPrecioNoche()) %></dd>
                        <dt class="col-sm-4 fs-5">Total</dt>
                        <dd class="col-sm-8 fs-5 fw-bold text-success"><%= money(r.getPrecioTotal()) %></dd>
                    </dl>

                    <form action="ReservaServlet" method="POST">
                        <input type="hidden" name="action" value="crear">
                        <input type="hidden" name="idHab" value="<%= hab.getIdHab() %>">
                        <input type="hidden" name="entrada" value="<%= r.getFechaEntrada() %>">
                        <input type="hidden" name="salida" value="<%= r.getFechaSalida() %>">
                        <button type="submit" class="btn btn-success w-100 py-2 fw-semibold">Confirmar reserva</button>
                    </form>
                    <a href="ReservaServlet?action=buscar&entrada=<%= r.getFechaEntrada() %>&salida=<%= r.getFechaSalida() %>"
                       class="btn btn-link w-100 mt-2 text-secondary">Volver a las habitaciones disponibles</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
