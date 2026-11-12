package ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import logic.Registro;
import logic.RegistroException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String VISTA_REGISTRO = "/WEB-INF/Vista/register.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Si ya tiene sesión iniciada no tiene sentido mostrar el formulario
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String nombreUsuario = request.getParameter("nombreUsuario");
        String contrasenia = request.getParameter("contrasenia");
        String confirmacion = request.getParameter("confirmacion");

        try {
            new Registro().registrar(dni, nombre, apellido, email, nombreUsuario, contrasenia, confirmacion);

            // Patrón PRG: redirigimos al login con un aviso para que F5 no reenvíe el formulario
            response.sendRedirect(request.getContextPath() + "/LoginServlet?registrado=1");

        } catch (RegistroException e) {
            // Volvemos al formulario con el error y los datos ya cargados (menos las contraseñas)
            request.setAttribute("error", e.getMessage());
            request.setAttribute("dni", dni);
            request.setAttribute("nombre", nombre);
            request.setAttribute("apellido", apellido);
            request.setAttribute("email", email);
            request.setAttribute("nombreUsuario", nombreUsuario);
            request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
        }
    }
}
