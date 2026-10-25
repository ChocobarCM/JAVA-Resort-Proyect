package ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Entities.Usuario;
import logic.Login;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String VISTA_LOGIN = "/WEB-INF/Vista/login.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Cerrar sesión
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Si ya está logueado no tiene sentido mostrar el formulario
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.getRequestDispatcher(VISTA_LOGIN).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nombreUsuario = request.getParameter("nombreUsuario");
        String contrasenia = request.getParameter("contrasenia");

        Usuario datos = new Usuario();
        datos.setNombreUsuario(nombreUsuario != null ? nombreUsuario.trim() : null);
        datos.setContrasenia(contrasenia);

        Usuario logueado = new Login().validate(datos);

        if (logueado == null) {
            request.setAttribute("error", "Usuario o contraseña incorrectos.");
            request.setAttribute("nombreUsuario", datos.getNombreUsuario());
            request.getRequestDispatcher(VISTA_LOGIN).forward(request, response);
            return;
        }

        // Evita fijación de sesión: si ya había una sesión, se le cambia el id
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        request.getSession().setAttribute("usuario", logueado);

        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}