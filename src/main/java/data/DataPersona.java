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
                    "SELECT idPersona, dni, nombreComp, rol FROM persona"
            );

            if (rs != null) {

                while (rs.next()) {

                    Persona p = new Persona();

                    p.setIdPersona(rs.getInt("idPersona"));
                    p.setDni(rs.getInt("dni"));
                    p.setNombreComp(rs.getString("nombreComp"));
                    p.setRol(rs.getString("rol"));

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
                            "SELECT idPersona, dni, nombreComp, rol " +
                            "FROM persona WHERE dni = ?"
                    );

            stmt.setInt(1, per.getDni());

            rs = stmt.executeQuery();

            if (rs != null && rs.next()) {

                p = new Persona();

                p.setIdPersona(rs.getInt("idPersona"));
                p.setDni(rs.getInt("dni"));
                p.setNombreComp(rs.getString("nombreComp"));
                p.setRol(rs.getString("rol"));
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
                            "INSERT INTO persona(dni, nombreComp, rol) " +
                            "VALUES (?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            stmt.setInt(1, p.getDni());
            stmt.setString(2, p.getNombreComp());
            stmt.setString(3, p.getRol());

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
}