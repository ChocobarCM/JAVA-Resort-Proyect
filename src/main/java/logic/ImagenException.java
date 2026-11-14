package logic;

/**
 * Error al manejar una imagen con un mensaje pensado para mostrarle al usuario
 * (formato no permitido, archivo muy grande, imagen inexistente, etc.).
 */
public class ImagenException extends Exception {
	private static final long serialVersionUID = 1L;

	public ImagenException(String mensaje) {
		super(mensaje);
	}

	public ImagenException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
