package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import entities.Habitacion;
import entities.ImagenHabitacion;

/**
 * Acceso a la tabla Imagen_Habitacion. Solo maneja las filas (nombre del archivo);
 * los archivos físicos los maneja logic.GestorImagenes.
 */
public class DataImagenHabitacion {

	private ImagenHabitacion mapear(ResultSet rs) throws SQLException {
		ImagenHabitacion img = new ImagenHabitacion();
		img.setIdImagen(rs.getInt("idImagen"));
		img.setIdHab(rs.getInt("idHab"));
		img.setNombreArchivo(rs.getString("nombreArchivo"));
		return img;
	}

	/** Imágenes de una habitación, de la más vieja (portada) a la más nueva. */
	public LinkedList<ImagenHabitacion> getByHabitacion(int idHab) throws SQLException {
		LinkedList<ImagenHabitacion> lista = new LinkedList<>();
		try (PreparedStatement stmt = DbConnector.getInstancia().getConn().prepareStatement(
				"SELECT idImagen, idHab, nombreArchivo FROM Imagen_Habitacion WHERE idHab = ? ORDER BY idImagen")) {
			stmt.setInt(1, idHab);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					lista.add(mapear(rs));
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
		return lista;
	}

	public ImagenHabitacion getById(int idImagen) throws SQLException {
		try (PreparedStatement stmt = DbConnector.getInstancia().getConn().prepareStatement(
				"SELECT idImagen, idHab, nombreArchivo FROM Imagen_Habitacion WHERE idImagen = ?")) {
			stmt.setInt(1, idImagen);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapear(rs) : null;
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	public void add(ImagenHabitacion img) throws SQLException {
		try (PreparedStatement stmt = DbConnector.getInstancia().getConn().prepareStatement(
				"INSERT INTO Imagen_Habitacion(idHab, nombreArchivo) VALUES (?, ?)",
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, img.getIdHab());
			stmt.setString(2, img.getNombreArchivo());
			stmt.executeUpdate();
			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys != null && keys.next()) {
					img.setIdImagen(keys.getInt(1));
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	public void delete(ImagenHabitacion img) throws SQLException {
		try (PreparedStatement stmt = DbConnector.getInstancia().getConn().prepareStatement(
				"DELETE FROM Imagen_Habitacion WHERE idImagen = ?")) {
			stmt.setInt(1, img.getIdImagen());
			stmt.executeUpdate();
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	/**
	 * Completa las imágenes de varias habitaciones con una sola consulta
	 * (en vez de una consulta por habitación al armar un listado).
	 */
	public void cargarImagenes(LinkedList<Habitacion> habitaciones) throws SQLException {
		if (habitaciones == null || habitaciones.isEmpty()) {
			return;
		}

		StringBuilder marcas = new StringBuilder();
		for (int i = 0; i < habitaciones.size(); i++) {
			marcas.append(i == 0 ? "?" : ", ?");
		}

		try (PreparedStatement stmt = DbConnector.getInstancia().getConn().prepareStatement(
				"SELECT idImagen, idHab, nombreArchivo FROM Imagen_Habitacion WHERE idHab IN (" + marcas
						+ ") ORDER BY idImagen")) {
			int i = 1;
			for (Habitacion hab : habitaciones) {
				stmt.setInt(i++, hab.getIdHab());
				hab.setImagenes(new LinkedList<>()); // por si ya traía algo
			}
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					ImagenHabitacion img = mapear(rs);
					for (Habitacion hab : habitaciones) {
						if (hab.getIdHab() == img.getIdHab()) {
							hab.getImagenes().add(img);
							break;
						}
					}
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}
}
