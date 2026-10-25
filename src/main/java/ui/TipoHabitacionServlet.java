package ui;

import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Entities.Tipo_Habitacion;
import data.DataTipoHabitacion;

@WebServlet("/TipoHabitacionServlet")
public class TipoHabitacionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        DataTipoHabitacion dth = new DataTipoHabitacion();

        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Tipo_Habitacion thBuscar = new Tipo_Habitacion();
            thBuscar.setIdTipoHab(id);
            
            Tipo_Habitacion tipoHabitacionEditar = dth.getById(thBuscar);
            request.setAttribute("tipoHabitacionEditar", tipoHabitacionEditar);
        }

        LinkedList<Tipo_Habitacion> lista = dth.getAll();
        request.setAttribute("listaTipoHabitacion", lista);
        
        request.getRequestDispatcher("/WEB-INF/Vista/tipo_habitacion.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        DataTipoHabitacion dth = new DataTipoHabitacion();

        if ("create".equals(action)) {
            Tipo_Habitacion th = new Tipo_Habitacion();
            th.setNomb(request.getParameter("nomb"));
            th.setDescripcion(request.getParameter("descripcion"));
            th.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
            th.setPrecioNoche(Double.parseDouble(request.getParameter("precioNoche")));
            
            dth.add(th);

        } else if ("update".equals(action)) {
            Tipo_Habitacion th = new Tipo_Habitacion();
            th.setIdTipoHab(Integer.parseInt(request.getParameter("idTipoHab")));
            th.setNomb(request.getParameter("nomb"));
            th.setDescripcion(request.getParameter("descripcion"));
            th.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
            th.setPrecioNoche(Double.parseDouble(request.getParameter("precioNoche")));
            
            dth.update(th);
            
        } else if ("delete".equals(action)) {
            Tipo_Habitacion th = new Tipo_Habitacion();
            th.setIdTipoHab(Integer.parseInt(request.getParameter("idTipoHab")));
            
            dth.delete(th);
        }

        response.sendRedirect("TipoHabitacionServlet?action=list");
    }
}