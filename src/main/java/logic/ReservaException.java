package logic;

/**
 * Error de reserva con un mensaje pensado para mostrarle al usuario
 * (fechas inválidas, habitación que ya no está disponible, etc.).
 */
public class ReservaException extends Exception {
	private static final long serialVersionUID = 1L;

	public ReservaException(String mensaje) {
		super(mensaje);
	}
}
