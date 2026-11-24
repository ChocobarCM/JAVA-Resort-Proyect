package ui;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import entities.Habitacion;
import entities.ImagenHabitacion;
import entities.Tipo_Habitacion;
import data.DataHabitacion;
import data.DataImagenHabitacion;
import data.DataTipoHabitacion;
import logic.GestorImagenes;
import logic.ImagenException;

/**
 * ABMC de habitaciones + imágenes.
 *
 * El formulario de alta/edición se envía como multipart (porque lleva archivos), por eso el
 * servlet declara @MultipartConfig. Los límites de acá son un tope del contenedor; las reglas
 * "de verdad" (formato, contenido, tamaño) las valida GestorImagenes.
 *
 *   POST action=create       -> alta de la habitación + sus imágenes
 *   POST action=update       -> modifica la habitación y agrega las imágenes nuevas
 *   POST action=deleteImage  -> elimina UNA imagen (fila + archivo)
 *   POST action=delete       -> elimina la habitación y, si se pudo, sus archivos
 */
@WebServlet("/HabitacionServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = GestorImagenes.TAMANIO_MAX,
        maxRequestSize = GestorImagenes.MAX_IMAGENES_POR_HABITACION * GestorImagenes.TAMANIO_MAX)
public class HabitacionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Nombre del campo <input type="file"> del formulario. */
    private static final String CAMPO_IMAGENES = "imagenes";
    /** Avisos que viajan por la sesión y se muestran una sola vez (después del redirect). */
    private static final String AVISOS = "avisosHabitacion";

    // EL MOZO SIRVIENDO LA PANTALLA
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        DataHabitacion dh = new DataHabitacion();
        DataTipoHabitacion dth = new DataTipoHabitacion();
        DataImagenHabitacion di = new DataImagenHabitacion();

        // Avisos del último POST (imágenes rechazadas, etc.): se muestran una vez y se borran
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AVISOS) != null) {
            request.setAttribute("avisos", session.getAttribute(AVISOS));
            session.removeAttribute(AVISOS);
        }

        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Habitacion habBuscar = new Habitacion();
                habBuscar.setIdHab(id);

                Habitacion habitacionEditar = dh.getById(habBuscar);
                if (habitacionEditar != null) {
                    habitacionEditar.setImagenes(di.getByHabitacion(id));
                }
                request.setAttribute("habitacionEditar", habitacionEditar);
            }

            LinkedList<Habitacion> listaHabitaciones = dh.getAll();
            di.cargarImagenes(listaHabitaciones);
            request.setAttribute("listaHabitaciones", listaHabitaciones);
        } catch (SQLException e) {
            // Lo más probable: falta crear la tabla Imagen_Habitacion (db/imagenesHabitacion.sql)
            throw new ServletException("No se pudieron cargar las imágenes de las habitaciones", e);
        }

        LinkedList<Tipo_Habitacion> listaTipos = dth.getAll();
        request.setAttribute("listaTipos", listaTipos);

        request.getRequestDispatcher("/WEB-INF/Vista/habitacion.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // antes de leer cualquier parámetro

        String action = request.getParameter("action");
        LinkedList<String> avisos = new LinkedList<>();

        if (action == null) {
            // Con multipart, si una imagen supera el máximo el contenedor descarta TODO el formulario
            avisos.add("No se pudo procesar el formulario. Verifique que cada imagen pese como máximo "
                    + (GestorImagenes.TAMANIO_MAX / (1024 * 1024)) + " MB.");
            redirigir(request, response, avisos, "HabitacionServlet?action=list");
            return;
        }

        DataHabitacion dh = new DataHabitacion();
        DataImagenHabitacion di = new DataImagenHabitacion();
        String destino = "HabitacionServlet?action=list";

        try {
            if ("create".equals(action)) {
                Habitacion hab = new Habitacion();
                hab.setNum(request.getParameter("num"));
                hab.setPiso(Integer.parseInt(request.getParameter("piso")));
                hab.setEstado(request.getParameter("estado"));

                Tipo_Habitacion th = new Tipo_Habitacion();
                th.setIdTipoHab(Integer.parseInt(request.getParameter("idTipoHab")));
                hab.setTipo_Habitacion(th);

                dh.add(hab);

                if (hab.getIdHab() > 0) {
                    guardarImagenes(request, hab.getIdHab(), avisos);
                } else if (hayImagenesEnviadas(request)) {
                    avisos.add("No se pudo guardar la habitación (revise que el número no esté repetido); las imágenes no se cargaron.");
                }

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
                guardarImagenes(request, hab.getIdHab(), avisos);

            } else if ("deleteImage".equals(action)) {
                int idHab = Integer.parseInt(request.getParameter("idHab"));
                int idImagen = Integer.parseInt(request.getParameter("idImagen"));
                destino = "HabitacionServlet?action=edit&id=" + idHab;

                ImagenHabitacion img = di.getById(idImagen);
                // se comprueba que la imagen sea de esa habitación: el id viene del navegador
                if (img != null && img.getIdHab() == idHab) {
                    di.delete(img);
                    new GestorImagenes().eliminar(img.getNombreArchivo());
                }

            } else if ("delete".equals(action)) {
                Habitacion hab = new Habitacion();
                hab.setIdHab(Integer.parseInt(request.getParameter("idHab")));

                // Hay que anotar los archivos ANTES: el ON DELETE CASCADE borra las filas junto con la habitación
                LinkedList<ImagenHabitacion> imagenes = di.getByHabitacion(hab.getIdHab());
                dh.delete(hab);

                // dh.delete no avisa si falló (ej: la habitación tiene reservas), así que se verifica.
                // Si la habitación sigue existiendo, sus imágenes tampoco se tocan.
                if (dh.getById(hab) == null) {
                    GestorImagenes gestor = new GestorImagenes();
                    for (ImagenHabitacion img : imagenes) {
                        gestor.eliminar(img.getNombreArchivo());
                    }
                } else {
                    avisos.add("No se pudo eliminar la habitación (puede tener reservas asociadas).");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            avisos.add("Error de base de datos al procesar las imágenes.");
        }

        redirigir(request, response, avisos, destino);
    }

    /**
     * Guarda cada imagen enviada en el campo "imagenes": primero el archivo (GestorImagenes valida y
     * copia) y después la fila en la base. Si falla la fila, se borra el archivo para no dejar huérfanos.
     * Una imagen inválida no frena a las demás: se anota en "avisos" y se sigue.
     */
    private void guardarImagenes(HttpServletRequest request, int idHab, LinkedList<String> avisos)
            throws ServletException, IOException, SQLException {

        DataImagenHabitacion di = new DataImagenHabitacion();
        GestorImagenes gestor = new GestorImagenes();
        int cupo = GestorImagenes.MAX_IMAGENES_POR_HABITACION - di.getByHabitacion(idHab).size();

        for (Part part : request.getParts()) {
            String nombreOriginal = part.getSubmittedFileName();
            // sin archivo elegido, el navegador igual manda una parte vacía: se ignora
            if (!CAMPO_IMAGENES.equals(part.getName()) || nombreOriginal == null || nombreOriginal.isBlank()) {
                continue;
            }

            if (cupo <= 0) {
                avisos.add(nombreOriginal + ": la habitación ya tiene el máximo de "
                        + GestorImagenes.MAX_IMAGENES_POR_HABITACION + " imágenes.");
                continue;
            }

            String archivo = null;
            try (InputStream in = part.getInputStream()) {
                archivo = gestor.guardar(in, nombreOriginal, part.getSize());

                ImagenHabitacion img = new ImagenHabitacion();
                img.setIdHab(idHab);
                img.setNombreArchivo(archivo);
                di.add(img);
                cupo--;

            } catch (ImagenException e) {
                avisos.add(nombreOriginal + ": " + e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                gestor.eliminar(archivo); // la fila no se guardó: el archivo quedaría huérfano
                avisos.add(nombreOriginal + ": no se pudo registrar en la base de datos.");
            }
        }
    }

    private boolean hayImagenesEnviadas(HttpServletRequest request) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            String nombre = part.getSubmittedFileName();
            if (CAMPO_IMAGENES.equals(part.getName()) && nombre != null && !nombre.isBlank()) {
                return true;
            }
        }
        return false;
    }

    /** Patrón PRG: guarda los avisos (si hay) en la sesión y redirige, para que F5 no reenvíe el formulario. */
    private void redirigir(HttpServletRequest request, HttpServletResponse response,
            LinkedList<String> avisos, String destino) throws IOException {
        if (!avisos.isEmpty()) {
            request.getSession().setAttribute(AVISOS, avisos);
        }
        response.sendRedirect(destino);
    }
}
