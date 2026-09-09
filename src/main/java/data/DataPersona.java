package data;

import Entities.Persona;

import java.sql.*;
import java.util.LinkedList;

public class DataPersona {

    public LinkedList<Persona> getAll() {

        Statement stmt = null;
        ResultSet rs = null;
        LinkedList<Persona> personas = new LinkedList<>();

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .createStatement();

            rs = stmt.executeQuery(
                    "SELECT idPersona, dni, nombre, apellido, email FROM persona"
            );

            if (rs != null) {

                while (rs.next()) {

                    Persona p = new Persona();

                    p.setIdPersona(rs.getInt("idPersona"));
                    p.setDni(rs.getInt("dni"));
                    p.setNombre(rs.getString("nombre"));
                    p.setApellido(rs.getString("apellido"));
                    p.setEmail(rs.getString("email"));

                    personas.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return personas;
    }


    public Persona getByDni(Persona per) {

        Persona p = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "SELECT idPersona, dni, nombre, apellido, email " +
                            "FROM persona WHERE dni = ?"
                    );

            stmt.setInt(1, per.getDni());

            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {

                p = new Persona();

                p.setIdPersona(rs.getInt("idPersona"));
                p.setDni(rs.getInt("dni"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setEmail(rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return p;
    }


    public void add(Persona p) {

        PreparedStatement stmt = null;
        ResultSet keyResultSet = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "INSERT INTO persona(dni, nombre, apellido, email) " +
                            "VALUES (?, ?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            stmt.setInt(1, p.getDni());
            stmt.setString(2, p.getNombre());
            stmt.setString(3, p.getApellido());
            stmt.setString(4, p.getEmail());

            stmt.executeUpdate();

            keyResultSet = stmt.getGeneratedKeys();

            if (keyResultSet != null && keyResultSet.next()) {

                p.setIdPersona(keyResultSet.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (keyResultSet != null) {
                    keyResultSet.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void update(Persona p) {

        PreparedStatement stmt = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "UPDATE persona " +
                            "SET dni = ?, nombre = ?, apellido = ?, email = ? " +
                            "WHERE idPersona = ?"
                    );

            stmt.setInt(1, p.getDni());
            stmt.setString(2, p.getNombre());
            stmt.setString(3, p.getApellido());
            stmt.setString(4, p.getEmail());
            stmt.setInt(5, p.getIdPersona());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (stmt != null) {
                    stmt.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void delete(Persona p) {

        PreparedStatement stmt = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "DELETE FROM persona WHERE idPersona = ?"
                    );

            stmt.setInt(1, p.getIdPersona());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (stmt != null) {
                    stmt.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}