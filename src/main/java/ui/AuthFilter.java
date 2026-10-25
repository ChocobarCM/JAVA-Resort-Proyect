package ui;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Entities.Usuario;

/**
 * Se ejecuta antes de cada request y decide si el usuario puede pasar.
 *
 *  1) Rutas públicas        -> pasa cualquiera (inicio, login, estáticos).
 *  2) Sin sesión iniciada   -> redirige al login (autenticación).
 *  3) Rutas solo ADMIN      -> si el rol no es ADMIN, responde 403 (autorización).
 *  4) Cualquier otra ruta   -> pasa si hay sesión iniciada.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

	public static final String ROL_ADMIN = "ADMIN";

	// Rutas accesibles sin iniciar sesión
	private static final Set<String> PUBLICAS = Set.of("", "/", "/index.jsp", "/LoginServlet");

	// Servlets de ABMC: solo el administrador. Al crear un servlet nuevo, se agrega acá.
	private static final Set<String> SOLO_ADMIN = Set.of(
			"/PersonaServlet"
			// "/HabitacionServlet", "/TipoHabitacionServlet", "/ServicioServlet", ...
	);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String path = request.getServletPath();

		if (PUBLICAS.contains(path)) {
			chain.doFilter(req, res);
			return;
		}

		HttpSession session = request.getSession(false);
		Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;

		if (usuario == null) {
			response.sendRedirect(request.getContextPath() + "/LoginServlet");
			return;
		}

		if (SOLO_ADMIN.contains(path) && !ROL_ADMIN.equals(usuario.getRol())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para acceder a esta sección.");
			return;
		}

		chain.doFilter(req, res);
	}
}