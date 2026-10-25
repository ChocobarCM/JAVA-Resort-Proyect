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

        // Si el usuario tocó el botón "Editar" en alguna fila de la tabla:
        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Persona pBuscar = new Persona();
            pBuscar.setIdPersona(id);
            
            Persona personaAEditar = dp.getById(pBuscar);
            // Mandamos esta persona al JSP para que rellene los cuadraditos del formulario
            request.setAttribute("personaEditar", personaAEditar);
        }

        // SIEMPRE cargamos la lista para que la tabla de la derecha no quede vacía
        LinkedList<Persona> personas = dp.getAll();
        request.setAttribute("listaPersonas", personas);
        
        request.getRequestDispatcher("/WEB-INF/Vista/personas.jsp").forward(request, response);
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

        } else if ("update".equals(action)) {
            Persona p = new Persona();
            // Acá sí necesitamos recuperar el ID oculto para saber a quién modificar en el WHERE
            p.setIdPersona(Integer.parseInt(request.getParameter("idPersona")));
            p.setDni(Integer.parseInt(request.getParameter("dni")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setEmail(request.getParameter("email"));
            dp.update(p);

        } else if ("delete".equals(action)) {
            Persona p = new Persona();
            p.setIdPersona(Integer.parseInt(request.getParameter("idPersona")));
            dp.delete(p);
        }

        // Patrón PRG (Post-Redirect-Get): Después de alterar la BD, redirigimos al doGet para refrescar limpio
        response.sendRedirect("PersonaServlet?action=list");
    }
}