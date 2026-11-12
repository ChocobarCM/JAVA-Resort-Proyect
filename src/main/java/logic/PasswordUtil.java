package logic;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 (viene en el JDK).
 * Formato guardado en la columna usuario.contrasenia:
 *     iteraciones:salt(base64):hash(base64)
 * El salt es aleatorio por contraseña y las iteraciones quedan guardadas,
 * así se pueden subir más adelante sin romper los hashes viejos.
 */
public final class PasswordUtil {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120_000;
	private static final int KEY_BITS = 256;
	private static final int SALT_BYTES = 16;

	private PasswordUtil() {}

	public static String hash(String password) {
		byte[] salt = new byte[SALT_BYTES];
		new SecureRandom().nextBytes(salt);
		byte[] hash = derive(password, salt, ITERATIONS);
		return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt)
				+ ":" + Base64.getEncoder().encodeToString(hash);
	}

	public static boolean verify(String password, String stored) {
		if (password == null || stored == null) {
			return false;
		}
		String[] parts = stored.split(":");
		if (parts.length != 3) {
			return false; // no es un hash válido (por ejemplo, contraseña vieja en texto plano)
		}
		try {
			int iterations = Integer.parseInt(parts[0]);
			byte[] salt = Base64.getDecoder().decode(parts[1]);
			byte[] expected = Base64.getDecoder().decode(parts[2]);
			byte[] actual = derive(password, salt, iterations);
			return MessageDigest.isEqual(expected, actual); // comparación en tiempo constante
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static byte[] derive(String password, byte[] salt, int iterations) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
		try {
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
		} finally {
			spec.clearPassword();
		}
	}

	/** Para generar un hash a mano: Run As > Java Application, con la contraseña como argumento. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Uso: PasswordUtil <contraseña>");
			return;
		}
		System.out.println(hash(args[0]));
	}
}