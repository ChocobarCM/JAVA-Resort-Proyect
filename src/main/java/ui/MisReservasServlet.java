package ui;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entities.Reserva;
import entities.Usuario;
import logic.ReservaException;
import logic.Reservas;

/**
 * "Mis Reservas" del cliente (cualquier usuario con sesión; el AuthFilter ya exige login).
 * Cada usuario ve y cancela únicamente sus propias reservas.
 *
 *   GET  -> lista las reservas del usuario logueado
 *   POST action=cancelar (idReserva) -> cancela y redirige (PRG) al listado con un aviso
 */
@WebServlet("/MisReservasServlet")
public class MisReservasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String VISTA = "/WEB-INF/Vista/mis_reservas.jsp";
	private static final String FLASH_OK = "misReservasOk";
	private static final String FLASH_ERROR = "misReservasError";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Usuario usuario = usuarioEnSesion(request);
		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/LoginServlet");
			return;
		}

		// Los avisos del POST viajan por la sesión y se muestran una sola vez
		HttpSession session = request.getSession(false);
		String ok = (String) session.getAttribute(FLASH_OK);
		String error = (String) session.getAttribute(FLASH_ERROR);
		session.removeAttribute(FLASH_OK);
		session.removeAttribute(FLASH_ERROR);

		try {
			LinkedList<Reserva> reservas = new Reservas().listarDeUsuario(usuario);
			request.setAttribute("reservas", reservas);
		} catch (ReservaException e) {
			if (error == null) {
				error = e.getMessage();
			}
		}

		request.setAttribute("ok", ok);
		request.setAttribute("error", error);
		request.getRequestDispatcher(VISTA).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		Usuario usuario = usuarioEnSesion(request);
		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/LoginServlet");
			return;
		}

		if ("cancelar".equals(request.getParameter("action"))) {
			HttpSession session = request.getSession();
			try {
				Reserva cancelada = new Reservas().cancelar(usuario, parseId(request.getParameter("idReserva")));
				session.setAttribute(FLASH_OK, "La reserva N° " + cancelada.getIdReserva() + " fue cancelada.");
			} catch (ReservaException e) {
				session.setAttribute(FLASH_ERROR, e.getMessage());
			}
		}

		response.sendRedirect(request.getContextPath() + "/MisReservasServlet");
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
