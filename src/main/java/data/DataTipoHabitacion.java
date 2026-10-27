package data;

import Entities.Tipo_Habitacion;
import java.sql.*;
import java.util.LinkedList;

public class DataTipoHabitacion {

    public LinkedList<Tipo_Habitacion> getAll() {

        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Tipo_Habitacion> tipos = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();

            rs = stmt.executeQuery(
                "SELECT idTipoHab, nomb, descripcion, capacidad, precioNoche FROM tipo_habitacion"
            );

            if (rs != null) {
                while (rs.next()) {
                    Tipo_Habitacion th = new Tipo_Habitacion();

                    th.setIdTipoHab(rs.getInt("idTipoHab"));
                    th.setNomb(rs.getString("nomb"));
                    th.setDescripcion(rs.getString("descripcion"));
                    th.setCapacidad(rs.getInt("capacidad"));
                    th.setPrecioNoche(rs.getDouble("precioNoche"));

                    tipos.add(th);
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

        return tipos;
    }

    public Tipo_Habitacion getById(Tipo_Habitacion tipoABuscar) {
        Tipo_Habitacion th = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT idTipoHab, nomb, descripcion, capacidad, precioNoche " +
                "FROM tipo_habitacion WHERE idTipoHab = ?"
            );

            stmt.setInt(1, tipoABuscar.getIdTipoHab());

            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                th = new Tipo_Habitacion();
                th.setIdTipoHab(rs.getInt("idTipoHab"));
                th.setNomb(rs.getString("nomb"));
                th.setDescripcion(rs.getString("descripcion"));
                th.setCapacidad(rs.getInt("capacidad"));
                th.setPrecioNoche(rs.getDouble("precioNoche"));
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

        return th;
    }

    public void add(Tipo_Habitacion th) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO tipo_habitacion(nomb, descripcion, capacidad, precioNoche) " +
                "VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, th.getNomb());         
            stmt.setString(2, th.getDescripcion());   
            stmt.setInt(3, th.getCapacidad());        
            stmt.setDouble(4, th.getPrecioNoche());   

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();
            if (keyResultSet != null && keyResultSet.next()) {
                th.setIdTipoHab(keyResultSet.getInt(1));
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

    public void update(Tipo_Habitacion th) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE tipo_habitacion " +
                "SET nomb = ?, descripcion = ?, capacidad = ?, precioNoche = ? " +
                "WHERE idTipoHab = ?"
            );

            stmt.setString(1, th.getNomb());
            stmt.setString(2, th.getDescripcion());
            stmt.setInt(3, th.getCapacidad());
            stmt.setDouble(4, th.getPrecioNoche());
            stmt.setInt(5, th.getIdTipoHab());

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

    public void delete(Tipo_Habitacion th) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "DELETE FROM tipo_habitacion WHERE idTipoHab = ?"
            );

            stmt.setInt(1, th.getIdTipoHab());

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