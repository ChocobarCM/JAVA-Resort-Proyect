package data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

import entities.Habitacion;
import entities.Persona;
import entities.Reserva;
import entities.Tipo_Habitacion;

public class DataReserva {

	public static final String ESTADO_CONFIRMADA = "CONFIRMADA";
	public static final String ESTADO_CANCELADA = "CANCELADA";

	/** Una habitación en este estado (lo fija el admin) no se puede reservar. */
	public static final String HAB_NO_RESERVABLE = "MANTENIMIENTO";

	/**
	 * Condición "habitación libre en el rango [entrada, salida)".
	 * Dos estadías se superponen si cada una empieza antes de que la otra termine:
	 *   reservaExistente.entrada < salidaNueva  Y  reservaExistente.salida > entradaNueva
	 * Así el día de salida de una reserva queda libre para la entrada de la siguiente.
	 * Las reservas CANCELADAS no bloquean la habitación.
	 * Parámetros, en orden: 1) salida  2) entrada.
	 */
	private static final String SQL_HAB_LIBRE =
			"h.estado <> '" + HAB_NO_RESERVABLE + "' " +
			"AND NOT EXISTS (SELECT 1 FROM Reserva r WHERE r.idHab = h.idHab " +
			"AND r.estado <> '" + ESTADO_CANCELADA + "' " +
			"AND r.fechaEntrada < ? AND r.fechaSalida > ?)";

	/** Reserva + habitación + tipo, para listar y consultar. */
	private static final String SQL_RESERVA_CON_HAB =
			"SELECT r.idReserva, r.idUsuario, r.fechaEntrada, r.fechaSalida, r.estado, r.precioTotal, " +
			"h.idHab, h.num, h.piso, h.estado AS estadoHab, " +
			"t.idTipoHab, t.nomb, t.descripcion, t.capacidad, t.precioNoche " +
			"FROM Reserva r " +
			"INNER JOIN Habitacion h ON r.idHab = h.idHab " +
			"INNER JOIN Tipo_Habitacion t ON h.idTipoHab = t.idTipoHab ";

	/**
	 * Cancela una reserva. Todas las condiciones van en el mismo UPDATE para que sea atómico:
	 * que sea del usuario, que siga CONFIRMADA y que la entrada sea posterior a "hoy".
	 * Parámetros, en orden: 1) idReserva  2) idUsuario  3) hoy
	 */
	private static final String SQL_CANCELAR =
			"UPDATE Reserva SET estado = '" + ESTADO_CANCELADA + "' " +
			"WHERE idReserva = ? AND idUsuario = ? " +
			"AND estado = '" + ESTADO_CONFIRMADA + "' AND fechaEntrada > ?";

	/** Habitaciones libres durante todo el rango [entrada, salida), de la más barata a la más cara. */
	public LinkedList<Habitacion> getDisponibles(LocalDate entrada, LocalDate salida) throws SQLException {
		LinkedList<Habitacion> lista = new LinkedList<>();

		Connection conn = conexion(); // si no hay conexión lanza SQLException (y no hay nada que liberar)
		try {
			try (PreparedStatement stmt = conn.prepareStatement(
					"SELECT h.idHab, h.num, h.piso, h.estado, " +
					"t.idTipoHab, t.nomb, t.descripcion, t.capacidad, t.precioNoche " +
					"FROM Habitacion h " +
					"INNER JOIN Tipo_Habitacion t ON h.idTipoHab = t.idTipoHab " +
					"WHERE " + SQL_HAB_LIBRE + " " +
					"ORDER BY t.precioNoche, h.num")) {

				stmt.setDate(1, Date.valueOf(salida));
				stmt.setDate(2, Date.valueOf(entrada));

				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Tipo_Habitacion th = new Tipo_Habitacion();
						th.setIdTipoHab(rs.getInt("idTipoHab"));
						th.setNomb(rs.getString("nomb"));
						th.setDescripcion(rs.getString("descripcion"));
						th.setCapacidad(rs.getInt("capacidad"));
						th.setPrecioNoche(rs.getDouble("precioNoche"));

						Habitacion hab = new Habitacion();
						hab.setIdHab(rs.getInt("idHab"));
						hab.setNum(rs.getString("num"));
						hab.setPiso(rs.getInt("piso"));
						hab.setEstado(rs.getString("estado"));
						hab.setTipo_Habitacion(th);

						lista.add(hab);
					}
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
		return lista;
	}

	/** ¿Esta habitación sigue libre en el rango [entrada, salida)? */
	public boolean estaDisponible(int idHab, LocalDate entrada, LocalDate salida) throws SQLException {
		Connection conn = conexion(); // si no hay conexión lanza SQLException (y no hay nada que liberar)
		try {
			return libre(conn, idHab, entrada, salida);
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	/**
	 * Registra la reserva en UNA transacción, evitando la doble reserva:
	 * 1) bloquea la fila de la habitación (FOR UPDATE): si dos personas reservan la misma
	 *    habitación a la vez, la segunda espera a que la primera termine;
	 * 2) vuelve a comprobar que siga libre (puede haberse ocupado desde que se la mostramos);
	 * 3) inserta la reserva.
	 * Usa una conexión propia (ver DbConnector.getNewConn).
	 *
	 * @return true si se guardó; false si la habitación ya no estaba libre (o dejó de existir)
	 * @throws SQLException ante un error de base de datos (se deshace la transacción)
	 */
	public boolean add(Reserva r) throws SQLException {
		Connection conn = null;

		try {
			conn = DbConnector.getInstancia().getNewConn();
			// Con READ COMMITTED cada consulta ve lo ya confirmado por las demás transacciones
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);

			int idHab = r.getHabitacion().getIdHab();

			// 1) Bloqueo de la habitación
			try (PreparedStatement lock = conn.prepareStatement(
					"SELECT idHab FROM Habitacion WHERE idHab = ? FOR UPDATE")) {
				lock.setInt(1, idHab);
				try (ResultSet rs = lock.executeQuery()) {
					if (!rs.next()) {
						conn.rollback();
						return false;
					}
				}
			}

			// 2) Re-chequeo de disponibilidad, ya con la habitación bloqueada
			if (!libre(conn, idHab, r.getFechaEntrada(), r.getFechaSalida())) {
				conn.rollback();
				return false;
			}

			// 3) Alta
			try (PreparedStatement stmt = conn.prepareStatement(
					"INSERT INTO Reserva(idUsuario, idHab, fechaEntrada, fechaSalida, estado, precioTotal) " +
					"VALUES (?, ?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS)) {

				stmt.setInt(1, r.getPersona().getIdPersona());
				stmt.setInt(2, idHab);
				stmt.setDate(3, Date.valueOf(r.getFechaEntrada()));
				stmt.setDate(4, Date.valueOf(r.getFechaSalida()));
				stmt.setString(5, r.getEstado());
				stmt.setBigDecimal(6, BigDecimal.valueOf(r.getPrecioTotal()).setScale(2, RoundingMode.HALF_UP));
				stmt.executeUpdate();

				try (ResultSet keys = stmt.getGeneratedKeys()) {
					if (keys.next()) {
						r.setIdReserva(keys.getInt(1));
					}
				}
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					e.addSuppressed(ex);
				}
			}
			throw e;

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** Todas las reservas de un usuario (también las canceladas), la de entrada más lejana primero. */
	public LinkedList<Reserva> getByUsuario(int idUsuario) throws SQLException {
		LinkedList<Reserva> lista = new LinkedList<>();

		Connection conn = conexion(); // si no hay conexión lanza SQLException (y no hay nada que liberar)
		try {
			try (PreparedStatement stmt = conn.prepareStatement(
					SQL_RESERVA_CON_HAB + "WHERE r.idUsuario = ? ORDER BY r.fechaEntrada DESC, r.idReserva DESC")) {
				stmt.setInt(1, idUsuario);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						lista.add(mapReserva(rs));
					}
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
		return lista;
	}

	/** Una reserva, pero solo si pertenece a ese usuario (si no, devuelve null). */
	public Reserva getByIdYUsuario(int idReserva, int idUsuario) throws SQLException {
		Connection conn = conexion(); // si no hay conexión lanza SQLException (y no hay nada que liberar)
		try {
			try (PreparedStatement stmt = conn.prepareStatement(
					SQL_RESERVA_CON_HAB + "WHERE r.idReserva = ? AND r.idUsuario = ?")) {
				stmt.setInt(1, idReserva);
				stmt.setInt(2, idUsuario);
				try (ResultSet rs = stmt.executeQuery()) {
					return rs.next() ? mapReserva(rs) : null;
				}
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	/**
	 * Pasa la reserva a CANCELADA (libera la habitación para esas fechas).
	 * @return true si se canceló; false si no correspondía (no es del usuario, ya estaba
	 *         cancelada o la entrada no es posterior a "hoy")
	 */
	public boolean cancelar(int idReserva, int idUsuario, LocalDate hoy) throws SQLException {
		Connection conn = conexion(); // si no hay conexión lanza SQLException (y no hay nada que liberar)
		try {
			try (PreparedStatement stmt = conn.prepareStatement(SQL_CANCELAR)) {
				stmt.setInt(1, idReserva);
				stmt.setInt(2, idUsuario);
				stmt.setDate(3, Date.valueOf(hoy));
				return stmt.executeUpdate() == 1;
			}
		} finally {
			DbConnector.getInstancia().releaseConn();
		}
	}

	// ------------------------------------------------------------------

	private Reserva mapReserva(ResultSet rs) throws SQLException {
		Tipo_Habitacion th = new Tipo_Habitacion();
		th.setIdTipoHab(rs.getInt("idTipoHab"));
		th.setNomb(rs.getString("nomb"));
		th.setDescripcion(rs.getString("descripcion"));
		th.setCapacidad(rs.getInt("capacidad"));
		th.setPrecioNoche(rs.getDouble("precioNoche"));

		Habitacion hab = new Habitacion();
		hab.setIdHab(rs.getInt("idHab"));
		hab.setNum(rs.getString("num"));
		hab.setPiso(rs.getInt("piso"));
		hab.setEstado(rs.getString("estadoHab"));
		hab.setTipo_Habitacion(th);

		// Del huésped solo se carga el id (el resto ya está en la sesión del usuario)
		Persona persona = new Persona();
		persona.setIdPersona(rs.getInt("idUsuario"));

		Reserva r = new Reserva();
		r.setIdReserva(rs.getInt("idReserva"));
		r.setPersona(persona);
		r.setHabitacion(hab);
		r.setFechaEntrada(rs.getDate("fechaEntrada").toLocalDate());
		r.setFechaSalida(rs.getDate("fechaSalida").toLocalDate());
		r.setEstado(rs.getString("estado"));
		r.setPrecioTotal(rs.getDouble("precioTotal"));
		return r;
	}

	private boolean libre(Connection conn, int idHab, LocalDate entrada, LocalDate salida) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(
				"SELECT 1 FROM Habitacion h WHERE h.idHab = ? AND " + SQL_HAB_LIBRE)) {
			stmt.setInt(1, idHab);
			stmt.setDate(2, Date.valueOf(salida));
			stmt.setDate(3, Date.valueOf(entrada));
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	/** DbConnector.getConn() devuelve null si no pudo conectar: lo convertimos en un error claro. */
	private Connection conexion() throws SQLException {
		Connection conn = DbConnector.getInstancia().getConn();
		if (conn == null) {
			throw new SQLException("No se pudo conectar a la base de datos");
		}
		return conn;
	}
}
