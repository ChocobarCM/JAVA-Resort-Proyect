package Entities;

public class Servicios {
    private int idServicio;
    private String nombre;
    private String desc;
    private double precioS;

    public Servicios() {}

	public int getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getPrecioS() {
		return precioS;
	}

	public void setPrecioS(double precioS) {
		this.precioS = precioS;
	}
    
}