package Entities;

public class Cliente extends Persona {
    private String telefono;
    private String dni;

    public Cliente() {
        super();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}
