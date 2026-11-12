package logic;

/**
 * Error de registro con un mensaje pensado para mostrarle al usuario
 * (dato inválido, DNI o usuario repetido, etc.).
 */
public class RegistroException extends Exception {
	private static final long serialVersionUID = 1L;

	public RegistroException(String mensaje) {
		super(mensaje);
	}
}
