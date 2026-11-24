package entities;

/**
 * Una imagen de una habitación. En la base se guarda solo el nombre del archivo;
 * la carpeta donde vive físicamente la define logic.GestorImagenes.
 */
public class ImagenHabitacion {
	private int idImagen;
	private int idHab;
	private String nombreArchivo;

	public ImagenHabitacion() {}

	public int getIdImagen() {
		return idImagen;
	}

	public void setIdImagen(int idImagen) {
		this.idImagen = idImagen;
	}

	public int getIdHab() {
		return idHab;
	}

	public void setIdHab(int idHab) {
		this.idHab = idHab;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
}
