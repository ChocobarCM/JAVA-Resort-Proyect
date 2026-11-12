package logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

import data.DataHabitacion;
import data.DataReserva;
import entities.Habitacion;
import entities.Persona;
import entities.Reserva;
import entities.Usuario;

/**
 * Caso de uso "Reservar": buscar habitaciones libres para un rango de fechas
 * y registrar la reserva del usuario logueado (el huésped).
 *
 * El precio y la disponibilidad los calcula SIEMPRE el servidor: del formulario
 * solo se toman la habitación y las fechas.
 */
public class Reservas {

	/** Tope de seguridad para la estadía (evita rangos absurdos). */
	public static final int MAX_NOCHES = 365;

	private final DataReserva dr;
	private final DataHabitacion dh;

	public Reservas() {
		this(new DataReserva(), new DataHabitacion());
	}

	/** Permite inyectar los DAO (por ejemplo, para probar las reglas sin base de datos). */
	public Reservas(DataReserva dr, DataHabitacion dh) {
		this.dr = dr;
		this.dh = dh;
	}

	/** Habitaciones libres en [entrada, salida), de la más barata a la más cara. */
	public LinkedList<Habitacion> buscarDisponibles(String entrada, String salida) throws ReservaException {
		LocalDate[] f = validarFechas(entrada, salida);

		try {
			return dr.getDisponibles(f[0], f[1]);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ReservaException("No se pudo consultar la disponibilidad. Intente nuevamente más tarde.");
		}
	}

	/**
	 * Arma la reserva (sin guardarla) para mostrarla en la pantalla de confirmación:
	 * valida fechas y disponibilidad, y calcula el total.
	 */
	public Reserva armarReserva(Usuario huesped, int idHab, String entrada, String salida) throws ReservaException {
		LocalDate[] f = validarFechas(entrada, salida);

		Habitacion buscar = new Habitacion();
		buscar.setIdHab(idHab);
		Habitacion hab = dh.getById(buscar);
		if (hab == null) {
			throw new ReservaException("La habitación seleccionada no existe.");
		}

		try {
			if (!dr.estaDisponible(idHab, f[0], f[1])) {
				throw new ReservaException(MSG_NO_DISPONIBLE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ReservaException("No se pudo consultar la disponibilidad. Intente nuevamente más tarde.");
		}

		long noches = noches(f[0], f[1]);
		double total = BigDecimal.valueOf(hab.getTipo_Habitacion().getPrecioNoche())
				.multiply(BigDecimal.valueOf(noches))
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue();

		// Copia solo los datos de la persona (no la contraseña ni el rol del usuario)
		Persona persona = new Persona();
		persona.setIdPersona(huesped.getIdPersona());
		persona.setDni(huesped.getDni());
		persona.setNombre(huesped.getNombre());
		persona.setApellido(huesped.getApellido());
		persona.setEmail(huesped.getEmail());

		Reserva r = new Reserva();
		r.setPersona(persona);
		r.setHabitacion(hab);
		r.setFechaEntrada(f[0]);
		r.setFechaSalida(f[1]);
		r.setEstado(DataReserva.ESTADO_CONFIRMADA);
		r.setPrecioTotal(total);
		return r;
	}

	/** Registra la reserva. La habitación queda ocupada para esas fechas (no para las demás). */
	public Reserva reservar(Usuario huesped, int idHab, String entrada, String salida) throws ReservaException {
		Reserva r = armarReserva(huesped, idHab, entrada, salida);

		try {
			if (!dr.add(r)) {
				throw new ReservaException(MSG_NO_DISPONIBLE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ReservaException("No se pudo registrar la reserva. Intente nuevamente más tarde.");
		}
		return r;
	}

	// ---------------------------- Mis reservas ----------------------------

	/** Reservas del usuario (la de entrada más lejana primero), incluidas las canceladas. */
	public LinkedList<Reserva> listarDeUsuario(Usuario usuario) throws ReservaException {
		try {
			return dr.getByUsuario(usuario.getIdPersona());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ReservaException("No se pudieron consultar sus reservas. Intente nuevamente más tarde.");
		}
	}

	/**
	 * Cancela una reserva del usuario. Solo puede cancelar las suyas, que estén CONFIRMADAS
	 * y hasta el día anterior a la entrada.
	 */
	public Reserva cancelar(Usuario usuario, int idReserva) throws ReservaException {
		LocalDate hoy = LocalDate.now();
		int idUsuario = usuario.getIdPersona();

		try {
			Reserva r = dr.getByIdYUsuario(idReserva, idUsuario);

			// Si la reserva es de otra persona se responde igual que si no existiera
			if (r == null) {
				throw new ReservaException("La reserva no existe.");
			}
			if (DataReserva.ESTADO_CANCELADA.equals(r.getEstado())) {
				throw new ReservaException("La reserva ya estaba cancelada.");
			}
			if (!esCancelable(r, hoy)) {
				throw new ReservaException("Ya no se puede cancelar: la estadía ya comenzó o comienza hoy.");
			}
			if (!dr.cancelar(idReserva, idUsuario, hoy)) {
				throw new ReservaException("No se pudo cancelar la reserva. Actualice la página e intente nuevamente.");
			}

			r.setEstado(DataReserva.ESTADO_CANCELADA);
			return r;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new ReservaException("No se pudo cancelar la reserva. Intente nuevamente más tarde.");
		}
	}

	/** Regla de cancelación: confirmada y con entrada posterior a hoy. */
	public static boolean esCancelable(Reserva r, LocalDate hoy) {
		return DataReserva.ESTADO_CONFIRMADA.equals(r.getEstado()) && r.getFechaEntrada().isAfter(hoy);
	}

	public static boolean esCancelable(Reserva r) {
		return esCancelable(r, LocalDate.now());
	}

	/** Estado para mostrarle al cliente, derivado del estado guardado y de las fechas. */
	public static String estadoVisible(Reserva r, LocalDate hoy) {
		if (DataReserva.ESTADO_CANCELADA.equals(r.getEstado())) {
			return "Cancelada";
		}
		if (hoy.isBefore(r.getFechaEntrada())) {
			return "Próxima";
		}
		if (hoy.isBefore(r.getFechaSalida())) { // el día de salida el huésped ya se retira
			return "En curso";
		}
		return "Finalizada";
	}

	public static String estadoVisible(Reserva r) {
		return estadoVisible(r, LocalDate.now());
	}

	// ------------------------------------------------------------------

	private static final String MSG_NO_DISPONIBLE =
			"La habitación ya no está disponible para esas fechas. Busque nuevamente.";

	/** @return la fecha, o null si está vacía o no tiene formato AAAA-MM-DD */
	public static LocalDate parsearFecha(String texto) {
		if (texto == null || texto.trim().isEmpty()) {
			return null;
		}
		try {
			return LocalDate.parse(texto.trim());
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	public static long noches(LocalDate entrada, LocalDate salida) {
		return ChronoUnit.DAYS.between(entrada, salida);
	}

	private LocalDate[] validarFechas(String entradaTxt, String salidaTxt) throws ReservaException {
		boolean vacias = (entradaTxt == null || entradaTxt.trim().isEmpty())
				|| (salidaTxt == null || salidaTxt.trim().isEmpty());
		if (vacias) {
			throw new ReservaException("Seleccione la fecha de entrada y la de salida.");
		}

		LocalDate entrada = parsearFecha(entradaTxt);
		LocalDate salida = parsearFecha(salidaTxt);
		if (entrada == null || salida == null) {
			throw new ReservaException("Las fechas ingresadas no son válidas.");
		}
		if (entrada.isBefore(LocalDate.now())) {
			throw new ReservaException("La fecha de entrada no puede ser anterior a hoy.");
		}
		if (!salida.isAfter(entrada)) {
			throw new ReservaException("La fecha de salida debe ser posterior a la de entrada.");
		}
		if (noches(entrada, salida) > MAX_NOCHES) {
			throw new ReservaException("La estadía no puede superar las " + MAX_NOCHES + " noches.");
		}
		return new LocalDate[] { entrada, salida };
	}
}
