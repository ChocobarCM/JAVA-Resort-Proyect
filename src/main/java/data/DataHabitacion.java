package data;

import Entities.Habitacion;
import Entities.Tipo_Habitacion;
import java.sql.*;
import java.util.LinkedList;

public class DataHabitacion {

    public LinkedList<Habitacion> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Habitacion> habitaciones = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();

            rs = stmt.executeQuery(
                "SELECT h.idHab, h.num, h.piso, h.estado, " +
                "t.idTipoHab, t.nomb, t.descripcion, t.capacidad, t.precioNoche " +
                "FROM habitacion h " +
                "INNER JOIN tipo_habitacion t ON h.idTipoHab = t.idTipoHab"
            );

            if (rs != null) {
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

                    habitaciones.add(hab);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return habitaciones;
    }

    public Habitacion getById(Habitacion habBuscar) {
        Habitacion hab = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT h.idHab, h.num, h.piso, h.estado, " +
                "t.idTipoHab, t.nomb, t.descripcion, t.capacidad, t.precioNoche " +
                "FROM habitacion h " +
                "INNER JOIN tipo_habitacion t ON h.idTipoHab = t.idTipoHab " +
                "WHERE h.idHab = ?"
            );

            stmt.setInt(1, habBuscar.getIdHab());
            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                Tipo_Habitacion th = new Tipo_Habitacion();
                th.setIdTipoHab(rs.getInt("idTipoHab"));
                th.setNomb(rs.getString("nomb"));
                th.setDescripcion(rs.getString("descripcion"));
                th.setCapacidad(rs.getInt("capacidad"));
                th.setPrecioNoche(rs.getDouble("precioNoche"));

                hab = new Habitacion();
                hab.setIdHab(rs.getInt("idHab"));
                hab.setNum(rs.getString("num"));
                hab.setPiso(rs.getInt("piso"));
                hab.setEstado(rs.getString("estado"));
                hab.setTipo_Habitacion(th);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hab;
    }

    // 3. ALTA
    public void add(Habitacion hab) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO habitacion(num, piso, estado, idTipoHab) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, hab.getNum());
            stmt.setInt(2, hab.getPiso());
            stmt.setString(3, hab.getEstado());
            stmt.setInt(4, hab.getTipo_Habitacion().getIdTipoHab());

            stmt.executeUpdate();
            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                hab.setIdHab(keyResultSet.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (keyResultSet != null) keyResultSet.close();
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Habitacion hab) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE habitacion SET num = ?, piso = ?, estado = ?, idTipoHab = ? WHERE idHab = ?"
            );

            stmt.setString(1, hab.getNum());
            stmt.setInt(2, hab.getPiso());
            stmt.setString(3, hab.getEstado());
            stmt.setInt(4, hab.getTipo_Habitacion().getIdTipoHab());
            
            stmt.setInt(5, hab.getIdHab()); 

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(Habitacion hab) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM habitacion WHERE idHab = ?"
            );
            stmt.setInt(1, hab.getIdHab());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                DbConnector.getInstancia().releaseConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
