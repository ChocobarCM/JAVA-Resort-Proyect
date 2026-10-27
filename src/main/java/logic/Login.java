package logic;

import java.util.LinkedList;

import Entities.*;
import data.*;

public class Login {
	private DataPersona dp;
	private DataUsuario du;

	public Login() {
		dp = new DataPersona();
		du = new DataUsuario();
	}

	public Usuario validate(Usuario u) {
		if (u == null || u.getNombreUsuario() == null || u.getContrasenia() == null) {
			return null;
		}

		Usuario encontrado = du.getByNombreUsuario(u);
		if (encontrado == null) {
			return null;
		}

		if (!"ACTIVO".equals(encontrado.getEstado())) {
			return null;
		}

		// la columna contrasenia guarda el hash (ver PasswordUtil), nunca la password en plano
		if (!PasswordUtil.verify(u.getContrasenia(), encontrado.getContrasenia())) {
			return null;
		}

		// la contraseña no debe viajar a la sesión ni a las vistas
		encontrado.setContrasenia(null);
		return encontrado;
	}

	public LinkedList<Persona> getAll() {
		return dp.getAll();
	}

	public Persona getByDni(Persona per) {
		return dp.getByDni(per);
	}
}