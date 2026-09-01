package Entities;

public class Usuario extends Persona {
    private String mail;
    private String contrasenia;

    public Usuario() {
        super();
    }

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
    
}

