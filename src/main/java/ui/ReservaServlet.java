package ui;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.DataImagenHabitacion;
import entities.Habitacion;
import entities.Reserva;
import entities.Usuario;
import logic.ReservaException;
import logic.Reservas;

/**
 * Pantalla "Reservar" del cliente (cualquier usuario con sesión; el AuthFilter ya exige login).
 *
 *   GET  (sin action)        -> formulario de fechas
 *   GET  action=buscar       -> lista las habitaciones libres para las fechas
 *   GET  action=confirmar    -> resumen (huésped, habitación, fechas, total) para confirmar
 *   POST action=crear        -> registra la reserva y redirige (PRG) a action=exito
 *   GET  action=exito        -> vuelve al formulario mostrando la reserva confirmada
 */
@WebServlet("/ReservaServlet")
public class ReservaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA_BUSCAR = "/WEB-INF/Vista/reservar.jsp";
	private static final String VISTA_CONFIRMAR = "/WEB-INF/Vista/reserva_confirmar.jsp";
	private static final String RESERVA_CREADA = "reservaCreada";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = usuarioEnSesion(request);
		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/LoginServlet");
			return;
		}

		String action = request.getParameter("action");
		String entrada = request.getParameter("entrada");
		String salida = request.getParameter("salida");

		if ("exito".equals(action)) {
			// La reserva recién creada viaja por la sesión y se muestra una sola vez
			HttpSession session = request.getSession(false);
			if (session != null) {
				request.setAttribute(RESERVA_CREADA, session.getAttribute(RESERVA_CREADA));
				session.removeAttribute(RESERVA_CREADA);
			}
			request.getRequestDispatcher(VISTA_BUSCAR).forward(request, response);

		} else if ("buscar".equals(action)) {
			mostrarBusqueda(request, response, entrada, salida, null);

		} else if ("confirmar".equals(action)) {
			try {
				Reserva reserva = new Reservas().armarReserva(usuario, parseId(request.getParameter("idHab")), entrada, salida);
				request.setAttribute("reserva", reserva);
				request.getRequestDispatcher(VISTA_CONFIRMAR).forward(request, response);
			} catch (ReservaException e) {
				mostrarBusqueda(request, response, entrada, salida, e.getMessage());
			}

		} else {
			request.getRequestDispatcher(VISTA_BUSCAR).forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		Usuario usuario = usuarioEnSesion(request);
		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/LoginServlet");
			return;
		}

		if (!"crear".equals(request.getParameter("action"))) {
			response.sendRedirect(request.getContextPath() + "/ReservaServlet");
			return;
		}

		String entrada = request.getParameter("entrada");
		String salida = request.getParameter("salida");

		try {
			Reserva reserva = new Reservas().reservar(usuario, parseId(request.getParameter("idHab")), entrada, salida);

			// Patrón PRG: redirigimos para que F5 no vuelva a enviar la reserva
			request.getSession().setAttribute(RESERVA_CREADA, reserva);
			response.sendRedirect(request.getContextPath() + "/ReservaServlet?action=exito");

		} catch (ReservaException e) {
			mostrarBusqueda(request, response, entrada, salida, e.getMessage());
		}
	}

	/** Muestra el formulario con las fechas cargadas y, si son válidas, las habitaciones libres. */
	private void mostrarBusqueda(HttpServletRequest request, HttpServletResponse response,
			String entrada, String salida, String error) throws ServletException, IOException {

		request.setAttribute("entradaTxt", entrada != null ? entrada : "");
		request.setAttribute("salidaTxt", salida != null ? salida : "");

		try {
			LinkedList<Habitacion> disponibles = new Reservas().buscarDisponibles(entrada, salida);
			// Fotos para mostrar junto a cada habitación. Si fallan, la búsqueda sigue funcionando sin ellas.
			try {
				new DataImagenHabitacion().cargarImagenes(disponibles);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			LocalDate fEntrada = Reservas.parsearFecha(entrada);
			LocalDate fSalida = Reservas.parsearFecha(salida);

			request.setAttribute("disponibles", disponibles);
			request.setAttribute("entrada", fEntrada);
			request.setAttribute("salida", fSalida);
			request.setAttribute("noches", Reservas.noches(fEntrada, fSalida));
		} catch (ReservaException e) {
			if (error == null) {
				error = e.getMessage();
			}
		}

		request.setAttribute("error", error);
		request.getRequestDispatcher(VISTA_BUSCAR).forward(request, response);
	}

	private Usuario usuarioEnSesion(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session != null) ? (Usuario) session.getAttribute("usuario") : null;
	}

	/** @return el id, o -1 si el parámetro falta o no es un número (-1 no existe en la base) */
	private int parseId(String texto) {
		try {
			return Integer.parseInt(texto.trim());
		} catch (RuntimeException e) {
			return -1;
		}
	}
}
