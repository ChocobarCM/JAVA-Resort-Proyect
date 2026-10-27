package logic;

import java.sql.SQLException;
import java.util.regex.Pattern;

import Entities.Persona;
import Entities.Usuario;
import data.DataPersona;
import data.DataUsuario;

/**
 * Caso de uso "Crear cuenta": valida los datos y da de alta la Persona
 * junto con su Usuario asociado, en una sola operación.
 */
public class Registro {

	// El rol y el estado los decide el servidor, NUNCA vienen del formulario:
	// si no, cualquiera podría registrarse como ADMIN.
	public static final String ROL_NUEVO = "CLIENTE";
	public static final String ESTADO_NUEVO = "ACTIVO";

	private static final Pattern DNI = Pattern.compile("\\d{7,8}");
	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern USUARIO = Pattern.compile("[A-Za-z0-9._-]{3,30}");

	private final DataPersona dp = new DataPersona();
	private final DataUsuario du = new DataUsuario();

	/**
	 * @return el usuario creado (con su idPersona; sin la contraseña)
	 * @throws RegistroException si algún dato es inválido o ya existe
	 */
	public Usuario registrar(String dni, String nombre, String apellido, String email,
			String nombreUsuario, String contrasenia, String confirmacion) throws RegistroException {

		dni = limpiar(dni);
		nombre = limpiar(nombre);
		apellido = limpiar(apellido);
		email = limpiar(email);
		nombreUsuario = limpiar(nombreUsuario);

		// ---------- Validaciones ----------
		if (!DNI.matcher(dni).matches()) {
			throw new RegistroException("El DNI debe tener 7 u 8 dígitos, sin puntos.");
		}
		if (nombre.isEmpty() || nombre.length() > 50) {
			throw new RegistroException("El nombre es obligatorio (máximo 50 caracteres).");
		}
		if (apellido.isEmpty() || apellido.length() > 50) {
			throw new RegistroException("El apellido es obligatorio (máximo 50 caracteres).");
		}
		if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
			throw new RegistroException("Ingrese un email válido (máximo 100 caracteres).");
		}
		if (!USUARIO.matcher(nombreUsuario).matches()) {
			throw new RegistroException(
					"El usuario debe tener entre 3 y 30 caracteres: letras, números, punto, guion o guion bajo.");
		}
		if (contrasenia == null || contrasenia.length() < 8 || contrasenia.length() > 100) {
			throw new RegistroException("La contraseña debe tener entre 8 y 100 caracteres.");
		}
		if (!contrasenia.equals(confirmacion)) {
			throw new RegistroException("Las contraseñas no coinciden.");
		}

		// ---------- Armado de la entidad ----------
		Usuario nuevo = new Usuario();
		nuevo.setDni(Integer.parseInt(dni));
		nuevo.setNombre(nombre);
		nuevo.setApellido(apellido);
		nuevo.setEmail(email);
		nuevo.setNombreUsuario(nombreUsuario);
		nuevo.setContrasenia(PasswordUtil.hash(contrasenia)); // se guarda el hash, nunca la clave en plano
		nuevo.setRol(ROL_NUEVO);
		nuevo.setEstado(ESTADO_NUEVO);

		// ---------- Duplicados (chequeo previo para dar un mensaje claro) ----------
		Persona porDni = new Persona();
		porDni.setDni(nuevo.getDni());
		if (dp.getByDni(porDni) != null) {
			throw new RegistroException("Ya existe una persona registrada con ese DNI.");
		}
		if (du.getByNombreUsuario(nuevo) != null) {
			throw new RegistroException("Ese nombre de usuario ya está en uso. Elija otro.");
		}

		// ---------- Alta (Persona + Usuario en una transacción) ----------
		try {
			du.add(nuevo);
		} catch (SQLException e) {
			// Si dos personas se registran a la vez con los mismos datos, el chequeo de
			// arriba no alcanza: la restricción UNIQUE de la base es la que lo frena.
			String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
			if (e.getErrorCode() == 1062 && msg.contains("nombreusuario")) {
				throw new RegistroException("Ese nombre de usuario ya está en uso. Elija otro.");
			}
			if (e.getErrorCode() == 1062 && msg.contains("dni")) {
				throw new RegistroException("Ya existe una persona registrada con ese DNI.");
			}
			e.printStackTrace();
			throw new RegistroException("No se pudo completar el registro. Intente nuevamente más tarde.");
		}

		nuevo.setContrasenia(null);
		return nuevo;
	}

	private static String limpiar(String s) {
		return s == null ? "" : s.trim();
	}
}
