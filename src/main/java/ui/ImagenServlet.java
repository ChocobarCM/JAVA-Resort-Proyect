package ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.DataImagenHabitacion;
import entities.ImagenHabitacion;
import logic.GestorImagenes;
import logic.ImagenException;

/**
 * Entrega al navegador el archivo de una imagen:  ImagenServlet?id=<idImagen>
 *
 * Las imágenes viven fuera de la carpeta web, así que no se pueden pedir por URL directa:
 * pasan siempre por acá. El id se busca en la base y de ahí sale el nombre del archivo,
 * el navegador nunca manda un nombre ni una ruta.
 * No está en las listas del AuthFilter: exige sesión iniciada, pero no rol de administrador
 * (los clientes también ven las fotos al reservar).
 */
@WebServlet("/ImagenServlet")
public class ImagenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id;
		try {
			id = Integer.parseInt(request.getParameter("id").trim());
		} catch (RuntimeException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			ImagenHabitacion img = new DataImagenHabitacion().getById(id);
			if (img == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			GestorImagenes gestor = new GestorImagenes();
			Path ruta = gestor.obtener(img.getNombreArchivo());

			response.setContentType(gestor.tipoMime(img.getNombreArchivo()));
			response.setContentLengthLong(Files.size(ruta));
			response.setHeader("Cache-Control", "private, max-age=86400"); // el archivo nunca cambia: tiene nombre único
			response.setHeader("X-Content-Type-Options", "nosniff");
			Files.copy(ruta, response.getOutputStream());

		} catch (ImagenException e) {
			// la fila existe pero el archivo no (borrado a mano, otra máquina, etc.)
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
