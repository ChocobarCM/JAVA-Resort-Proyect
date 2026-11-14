package logic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Maneja los archivos de imagen en disco: guardar, recuperar, eliminar y validar.
 * No sabe nada de servlets ni de la base de datos.
 *
 * La carpeta NO está dentro del proyecto desplegado (Tomcat regenera esa copia y se perderían
 * las subidas). Por defecto es  <carpeta del usuario>/resort-imagenes/habitaciones  y se puede
 * cambiar con la propiedad de la JVM  -Dresort.imagenes.dir=D:/mis/imagenes
 *
 * Seguridad: el nombre que se guarda lo genera el sistema (UUID); el nombre que manda el
 * usuario jamás se usa para armar la ruta, así no hay forma de escribir fuera de la carpeta.
 */
public class GestorImagenes {

	public static final long TAMANIO_MAX = 5L * 1024 * 1024; // 5 MB por imagen
	public static final int MAX_IMAGENES_POR_HABITACION = 10;
	public static final String PROP_CARPETA = "resort.imagenes.dir";

	private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "gif", "webp");

	private final Path carpeta;

	public GestorImagenes() {
		this(carpetaPorDefecto());
	}

	public GestorImagenes(Path carpeta) {
		this.carpeta = carpeta.toAbsolutePath().normalize();
	}

	public static Path carpetaPorDefecto() {
		String configurada = System.getProperty(PROP_CARPETA);
		if (configurada != null && !configurada.isBlank()) {
			return Paths.get(configurada);
		}
		return Paths.get(System.getProperty("user.home"), "resort-imagenes", "habitaciones");
	}

	public Path getCarpeta() {
		return carpeta;
	}

	// ------------------------------------------------------------------ GUARDAR

	/**
	 * Valida y guarda una imagen.
	 *
	 * @param origen         contenido del archivo (se cierra al terminar)
	 * @param nombreOriginal nombre que tenía en la computadora del usuario (solo se usa para validar la extensión)
	 * @param tamanio        tamaño informado, en bytes
	 * @return el nombre con el que quedó guardado (es lo que se persiste en la base)
	 */
	public String guardar(InputStream origen, String nombreOriginal, long tamanio) throws ImagenException {
		if (origen == null || tamanio <= 0) {
			throw new ImagenException("El archivo está vacío.");
		}
		if (tamanio > TAMANIO_MAX) {
			throw new ImagenException("Supera el tamaño máximo de " + (TAMANIO_MAX / (1024 * 1024)) + " MB.");
		}
		if (!EXTENSIONES_PERMITIDAS.contains(extensionDe(nombreOriginal))) {
			throw new ImagenException("Formato no permitido (solo JPG, PNG, GIF o WEBP).");
		}

		Path destino = null;
		try (BufferedInputStream in = new BufferedInputStream(origen)) {
			// La extensión se puede falsear (un .exe renombrado a .jpg): se mira el contenido real
			in.mark(16);
			byte[] cabecera = in.readNBytes(12);
			in.reset();
			String extension = extensionReal(cabecera);
			if (extension == null) {
				throw new ImagenException("El contenido no es una imagen válida (JPG, PNG, GIF o WEBP).");
			}

			Files.createDirectories(carpeta);
			destino = carpeta.resolve(UUID.randomUUID() + "." + extension);
			copiarConLimite(in, destino);
			return destino.getFileName().toString();

		} catch (ImagenException e) {
			borrarSiExiste(destino);
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			borrarSiExiste(destino);
			throw new ImagenException("No se pudo guardar el archivo en el servidor.", e);
		}
	}

	/** Copia al disco sin pasar nunca el máximo, aunque el tamaño informado no sea real. */
	private void copiarConLimite(InputStream in, Path destino) throws IOException, ImagenException {
		long total = 0;
		byte[] buffer = new byte[8192];
		try (OutputStream out = Files.newOutputStream(destino, StandardOpenOption.CREATE_NEW)) {
			int leidos;
			while ((leidos = in.read(buffer)) != -1) {
				total += leidos;
				if (total > TAMANIO_MAX) {
					throw new ImagenException("Supera el tamaño máximo de " + (TAMANIO_MAX / (1024 * 1024)) + " MB.");
				}
				out.write(buffer, 0, leidos);
			}
		}
	}

	// ----------------------------------------------------------------- RECUPERAR

	/** @return la ruta del archivo, listo para leerlo/enviarlo. Falla si no existe. */
	public Path obtener(String nombreArchivo) throws ImagenException {
		Path ruta = resolverSeguro(nombreArchivo);
		if (!Files.isRegularFile(ruta)) {
			throw new ImagenException("La imagen no existe en el servidor.");
		}
		return ruta;
	}

	public boolean existe(String nombreArchivo) {
		try {
			return Files.isRegularFile(resolverSeguro(nombreArchivo));
		} catch (ImagenException e) {
			return false;
		}
	}

	/** Content-Type según la extensión (que ya fue validada contra el contenido al guardar). */
	public String tipoMime(String nombreArchivo) {
		switch (extensionDe(nombreArchivo)) {
		case "png":
			return "image/png";
		case "gif":
			return "image/gif";
		case "webp":
			return "image/webp";
		default:
			return "image/jpeg";
		}
	}

	// ------------------------------------------------------------------ ELIMINAR

	/** @return true si el archivo existía y se borró. Si ya no estaba, no es un error. */
	public boolean eliminar(String nombreArchivo) {
		if (nombreArchivo == null) {
			return false;
		}
		try {
			return Files.deleteIfExists(resolverSeguro(nombreArchivo));
		} catch (ImagenException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void borrarSiExiste(Path ruta) {
		if (ruta != null) {
			try {
				Files.deleteIfExists(ruta);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ------------------------------------------------------------------- VALIDAR

	/** Arma la ruta dentro de la carpeta y comprueba que no se escape de ella (ej: "../../algo"). */
	private Path resolverSeguro(String nombreArchivo) throws ImagenException {
		if (nombreArchivo == null || nombreArchivo.isBlank()) {
			throw new ImagenException("Nombre de archivo inválido.");
		}
		try {
			Path ruta = carpeta.resolve(nombreArchivo).normalize();
			if (!carpeta.equals(ruta.getParent())) {
				throw new ImagenException("Nombre de archivo inválido.");
			}
			return ruta;
		} catch (InvalidPathException e) {
			throw new ImagenException("Nombre de archivo inválido.");
		}
	}

	private static String extensionDe(String nombre) {
		if (nombre == null) {
			return "";
		}
		int punto = nombre.lastIndexOf('.');
		return punto < 0 ? "" : nombre.substring(punto + 1).trim().toLowerCase(Locale.ROOT);
	}

	/** Reconoce el formato por los primeros bytes ("firma" del archivo). null si no es una imagen soportada. */
	private static String extensionReal(byte[] h) {
		if (h.length >= 3 && b(h, 0) == 0xFF && b(h, 1) == 0xD8 && b(h, 2) == 0xFF) {
			return "jpg";
		}
		if (h.length >= 8 && b(h, 0) == 0x89 && b(h, 1) == 'P' && b(h, 2) == 'N' && b(h, 3) == 'G'
				&& b(h, 4) == 0x0D && b(h, 5) == 0x0A && b(h, 6) == 0x1A && b(h, 7) == 0x0A) {
			return "png";
		}
		if (h.length >= 6 && b(h, 0) == 'G' && b(h, 1) == 'I' && b(h, 2) == 'F' && b(h, 3) == '8'
				&& (b(h, 4) == '7' || b(h, 4) == '9') && b(h, 5) == 'a') {
			return "gif";
		}
		if (h.length >= 12 && b(h, 0) == 'R' && b(h, 1) == 'I' && b(h, 2) == 'F' && b(h, 3) == 'F'
				&& b(h, 8) == 'W' && b(h, 9) == 'E' && b(h, 10) == 'B' && b(h, 11) == 'P') {
			return "webp";
		}
		return null;
	}

	private static int b(byte[] datos, int i) {
		return datos[i] & 0xFF;
	}
}
