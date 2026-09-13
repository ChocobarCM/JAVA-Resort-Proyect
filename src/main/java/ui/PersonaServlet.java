package ui;

import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Entities.Persona;
import data.DataPersona;

@WebServlet("/PersonaServlet")
public class PersonaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        DataPersona dp = new DataPersona();

        if (action == null || action.equals("list")) {
            LinkedList<Persona> personas = dp.getAll();
            request.setAttribute("listaPersonas", personas);
            request.getRequestDispatcher("personas.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        DataPersona dp = new DataPersona();

        if ("create".equals(action)) {
            Persona p = new Persona();
            p.setDni(Integer.parseInt(request.getParameter("dni")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setEmail(request.getParameter("email"));

            dp.add(p);

            response.sendRedirect("PersonaServlet?action=list");
        }
    }
}