package ui;

import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Entities.Habitacion;
import Entities.Tipo_Habitacion;
import data.DataHabitacion;
import data.DataTipoHabitacion;

@WebServlet("/HabitacionServlet")
public class HabitacionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // EL MOZO SIRVIENDO LA PANTALLA
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        DataHabitacion dh = new DataHabitacion();
        DataTipoHabitacion dth = new DataTipoHabitacion(); 

        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Habitacion habBuscar = new Habitacion();
            habBuscar.setIdHab(id);
            
            Habitacion habitacionEditar = dh.getById(habBuscar);
            request.setAttribute("habitacionEditar", habitacionEditar);
        }

        LinkedList<Habitacion> listaHabitaciones = dh.getAll();
        request.setAttribute("listaHabitaciones", listaHabitaciones);
        
        LinkedList<Tipo_Habitacion> listaTipos = dth.getAll();
        request.setAttribute("listaTipos", listaTipos);
        
        request.getRequestDispatcher("/WEB-INF/Vista/habitaciones.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        DataHabitacion dh = new DataHabitacion();

        if ("create".equals(action)) {
            Habitacion hab = new Habitacion();
            hab.setNum(request.getParameter("num"));
            hab.setPiso(Integer.parseInt(request.getParameter("piso")));
            hab.setEstado(request.getParameter("estado"));
            
            Tipo_Habitacion th = new Tipo_Habitacion();
            th.setIdTipoHab(Integer.parseInt(request.getParameter("idTipoHab")));
            hab.setTipo_Habitacion(th);
            
            dh.add(hab);

        } else if ("update".equals(action)) {
            Habitacion hab = new Habitacion();
            hab.setIdHab(Integer.parseInt(request.getParameter("idHab")));
            hab.setNum(request.getParameter("num"));
            hab.setPiso(Integer.parseInt(request.getParameter("piso")));
            hab.setEstado(request.getParameter("estado"));
            
            Tipo_Habitacion th = new Tipo_Habitacion();
            th.setIdTipoHab(Integer.parseInt(request.getParameter("idTipoHab")));
            hab.setTipo_Habitacion(th);
            
            dh.update(hab);
            
        } else if ("delete".equals(action)) {
            Habitacion hab = new Habitacion();
            hab.setIdHab(Integer.parseInt(request.getParameter("idHab")));
            dh.delete(hab);
        }

        response.sendRedirect("HabitacionServlet?action=list");
    }
}