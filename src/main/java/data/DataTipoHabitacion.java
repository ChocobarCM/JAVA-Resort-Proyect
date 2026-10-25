package data;

import Entities.Tipo_Habitacion;
import java.sql.*;
import java.util.LinkedList;

public class DataTipoHabitacion {

    // 1. OBTENER TODOS (Para llenar la tabla del JSP y futuros desplegables)
    public LinkedList<Tipo_Habitacion> getAll() {

        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Tipo_Habitacion> tipos = new LinkedList<>();

        try {
            stmt = DbConnector.getInstancia().getConn().createStatement();

            // Pedimos las 5 columnas reales de la tabla tipo_habitacion
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

                    // Agregamos el objeto "th" a la lista "tipos"
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

    // 2. BUSCAR POR ID (Fundamental para cuando toqués el botón "Editar" en la tabla)
    public Tipo_Habitacion getById(Tipo_Habitacion tipoABuscar) {
        Tipo_Habitacion th = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "SELECT idTipoHab, nomb, descripcion, capacidad, precioNoche " +
                "FROM tipo_habitacion WHERE idTipoHab = ?"
            );

            // Reemplazamos el único signo de pregunta (?) por el ID que queremos buscar
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

    // 3. ALTA (Guardar un nuevo Tipo de Habitación)
    public void add(Tipo_Habitacion th) {
        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {
            // No incluimos idTipoHab en el INSERT porque es autoincremental en MySQL
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "INSERT INTO tipo_habitacion(nomb, descripcion, capacidad, precioNoche) " +
                "VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            // Llenamos los 4 signos de pregunta en el mismo orden del INSERT:
            stmt.setString(1, th.getNomb());          // 1° -> nomb (String)
            stmt.setString(2, th.getDescripcion());   // 2° -> descripcion (String)
            stmt.setInt(3, th.getCapacidad());        // 3° -> capacidad (int)
            stmt.setDouble(4, th.getPrecioNoche());   // 4° -> precioNoche (double)

            stmt.executeUpdate();

            // Recuperamos el ID autonumérico que le asignó MySQL
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

    // 4. MODIFICACIÓN (Actualizar un registro existente)
    public void update(Tipo_Habitacion th) {
        PreparedStatement stmt = null;

        try {
            stmt = DbConnector.getInstancia().getConn().prepareStatement(
                "UPDATE tipo_habitacion " +
                "SET nomb = ?, descripcion = ?, capacidad = ?, precioNoche = ? " +
                "WHERE idTipoHab = ?"
            );

            // Los primeros 4 signos (?) son los datos nuevos:
            stmt.setString(1, th.getNomb());
            stmt.setString(2, th.getDescripcion());
            stmt.setInt(3, th.getCapacidad());
            stmt.setDouble(4, th.getPrecioNoche());
            // El 5° signo (?) es el del WHERE para saber cuál fila modificar:
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

    // 5. BAJA (Eliminar por ID)
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