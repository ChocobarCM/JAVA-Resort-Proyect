package data;

import Entities.Usuario;

import java.sql.*;
import java.util.LinkedList;

public class DataUsuario {

	public LinkedList<Usuario> getAll() {

	    Statement stmt = null;
	    ResultSet rs = null;
	    LinkedList<Usuario> usuarios = new LinkedList<>();

	    try {

	        stmt = DbConnector.getInstancia()
	                .getConn()
	                .createStatement();

	        rs = stmt.executeQuery(
	                "SELECT p.idPersona, p.dni, p.nombre, p.apellido, p.email, " +
	                "u.nombreUsuario, u.contrasenia, u.rol, u.estado " +
	                "FROM persona p " +
	                "INNER JOIN usuario u ON p.idPersona = u.idPersona " +
	                "WHERE u.estado = 'ACTIVO'"
	        );

	        if (rs != null) {

	            while (rs.next()) {

	                Usuario u = new Usuario();

	                // Atributos heredados de Persona
	                u.setIdPersona(rs.getInt("idPersona"));
	                u.setDni(rs.getInt("dni"));
	                u.setNombre(rs.getString("nombre"));
	                u.setApellido(rs.getString("apellido"));
	                u.setEmail(rs.getString("email"));

	                // Atributos propios de Usuario
	                u.setNombreUsuario(rs.getString("nombreUsuario"));
	                u.setContrasenia(rs.getString("contrasenia"));
	                u.setRol(rs.getString("rol"));
	                u.setEstado(rs.getString("estado"));

	                usuarios.add(u);
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

	    return usuarios;
	}


	public Usuario getByNombreUsuario(Usuario usu) {

	    Usuario u = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {

	        stmt = DbConnector.getInstancia()
	                .getConn()
	                .prepareStatement(
	                        "SELECT p.idPersona, p.dni, p.nombre, p.apellido, p.email, " +
	                        "u.nombreUsuario, u.contrasenia, u.rol, u.estado " +
	                        "FROM persona p " +
	                        "INNER JOIN usuario u ON p.idPersona = u.idPersona " +
	                        "WHERE u.nombreUsuario = ?"
	                );

	        stmt.setString(1, usu.getNombreUsuario());

	        rs = stmt.executeQuery();

	        if (rs != null && rs.next()) {

	            u = new Usuario();

	            // Atributos heredados
	            u.setIdPersona(rs.getInt("idPersona"));
	            u.setDni(rs.getInt("dni"));
	            u.setNombre(rs.getString("nombre"));
	            u.setApellido(rs.getString("apellido"));
	            u.setEmail(rs.getString("email"));

	            // Atributos propios
	            u.setNombreUsuario(rs.getString("nombreUsuario"));
	            u.setContrasenia(rs.getString("contrasenia"));
	            u.setRol(rs.getString("rol"));
	            u.setEstado(rs.getString("estado"));
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

	    return u;
	}


	public void add(Usuario u) {

	    PreparedStatement stmtPersona = null;
	    PreparedStatement stmtUsuario = null;
	    ResultSet keyResultSet = null;

	    try {

	        // Primero insertamos la Persona
	        stmtPersona = DbConnector.getInstancia()
	                .getConn()
	                .prepareStatement(
	                        "INSERT INTO persona(dni, nombre, apellido, email) " +
	                        "VALUES (?, ?, ?, ?)",
	                        PreparedStatement.RETURN_GENERATED_KEYS
	                );

	        stmtPersona.setInt(1, u.getDni());
	        stmtPersona.setString(2, u.getNombre());
	        stmtPersona.setString(3, u.getApellido());
	        stmtPersona.setString(4, u.getEmail());

	        stmtPersona.executeUpdate();

	        keyResultSet = stmtPersona.getGeneratedKeys();

	        if (keyResultSet != null && keyResultSet.next()) {
	            u.setIdPersona(keyResultSet.getInt(1));
	        }

	        // Después insertamos el Usuario
	        stmtUsuario = DbConnector.getInstancia()
	                .getConn()
	                .prepareStatement(
	                        "INSERT INTO usuario(" +
	                        "idPersona, nombreUsuario, contrasenia, rol, estado" +
	                        ") VALUES (?, ?, ?, ?, ?)"
	                );

	        stmtUsuario.setInt(1, u.getIdPersona());
	        stmtUsuario.setString(2, u.getNombreUsuario());
	        stmtUsuario.setString(3, u.getContrasenia());
	        stmtUsuario.setString(4, u.getRol());
	        stmtUsuario.setString(5, u.getEstado());

	        stmtUsuario.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();

	    } finally {

	        try {

	            if (keyResultSet != null) {
	                keyResultSet.close();
	            }

	            if (stmtPersona != null) {
	                stmtPersona.close();
	            }

	            if (stmtUsuario != null) {
	                stmtUsuario.close();
	            }

	            DbConnector.getInstancia().releaseConn();

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
    public void update(Usuario u) {

        PreparedStatement stmtPersona = null;
        PreparedStatement stmtUsuario = null;

        try {

            // Actualizar datos de Persona
            stmtPersona = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "UPDATE persona " +
                            "SET dni = ?, nombre = ?, apellido = ?, email = ? " +
                            "WHERE idPersona = ?"
                    );

            stmtPersona.setInt(1, u.getDni());
            stmtPersona.setString(2, u.getNombre());
            stmtPersona.setString(3, u.getApellido());
            stmtPersona.setString(4, u.getEmail());
            stmtPersona.setInt(5, u.getIdPersona());

            stmtPersona.executeUpdate();


            // Actualizar datos de Usuario
            stmtUsuario = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "UPDATE usuario " +
                            "SET nombreUsuario = ?, contrasenia = ?, rol = ?, estado = ? " +
                            "WHERE idPersona = ?"
                    );

            stmtUsuario.setString(1, u.getNombreUsuario());
            stmtUsuario.setString(2, u.getContrasenia());
            stmtUsuario.setString(3, u.getRol());
            stmtUsuario.setString(4, u.getEstado());
            stmtUsuario.setInt(5, u.getIdPersona());

            stmtUsuario.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            try {

                if (stmtPersona != null) {
                    stmtPersona.close();
                }

                if (stmtUsuario != null) {
                    stmtUsuario.close();
                }

                DbConnector.getInstancia().releaseConn();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteLogico(Usuario u) {

        PreparedStatement stmt = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "UPDATE usuario " +
                            "SET estado = 'INACTIVO' " +
                            "WHERE idPersona = ?"
                    );

            stmt.setInt(1, u.getIdPersona());

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
    public void activar(Usuario u) {

        PreparedStatement stmt = null;

        try {

            stmt = DbConnector.getInstancia()
                    .getConn()
                    .prepareStatement(
                            "UPDATE usuario " +
                            "SET estado = 'ACTIVO' " +
                            "WHERE idPersona = ?"
                    );

            stmt.setInt(1, u.getIdPersona());

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