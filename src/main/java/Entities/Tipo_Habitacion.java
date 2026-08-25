package Entities;

public class Tipo_Habitacion {
    private int idTipoHab;
    private String nomb;
    private int capacidad;
    private double precioNoche;

    public Tipo_Habitacion() {}

	public int getIdTipoHab() {
		return idTipoHab;
	}

	public void setIdTipoHab(int idTipoHab) {
		this.idTipoHab = idTipoHab;
	}

	public String getNomb() {
		return nomb;
	}

	public void setNomb(String nomb) {
		this.nomb = nomb;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public double getPrecioNoche() {
		return precioNoche;
	}

	public void setPrecioNoche(double precioNoche) {
		this.precioNoche = precioNoche;
	}
    
}