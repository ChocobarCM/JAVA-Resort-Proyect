package Entities;

import java.time.LocalDate;

public class Actividad {
    private int idAct;
    private String nomb;
    private String desc;
    private int cupo;
    private int cupoMax;
    private double precioAct;
    private LocalDate fecha;
    private Coordinador coordinador; 
    public Actividad() {}
	public int getIdAct() {
		return idAct;
	}
	public void setIdAct(int idAct) {
		this.idAct = idAct;
	}
	public String getNomb() {
		return nomb;
	}
	public void setNomb(String nomb) {
		this.nomb = nomb;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getCupo() {
		return cupo;
	}
	public void setCupo(int cupo) {
		this.cupo = cupo;
	}
	public int getCupoMax() {
		return cupoMax;
	}
	public void setCupoMax(int cupoMax) {
		this.cupoMax = cupoMax;
	}
	public double getPrecioAct() {
		return precioAct;
	}
	public void setPrecioAct(double precioAct) {
		this.precioAct = precioAct;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public Coordinador getCoordinador() {
		return coordinador;
	}
	public void setCoordinador(Coordinador coordinador) {
		this.coordinador = coordinador;
	}
    
}
