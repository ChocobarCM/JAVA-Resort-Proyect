package Entities;

import java.time.LocalDate;
import java.util.LinkedList;

public class Publicacion {
    private int idPublicacion;
    private int cantServ;
    private int cantAct;
    private int cantNoches;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private String estado;
    private double precio;

    
    private Tipo_Habitacion tipoHabitacion;        
    private LinkedList<Servicios> Servicios;        
    private LinkedList<Actividad> actividades;    

    public Publicacion() {
        Servicios = new LinkedList<>();
        actividades = new LinkedList<>();
    }

	public int getIdPublicacion() {
		return idPublicacion;
	}

	public void setIdPublicacion(int idPublicacion) {
		this.idPublicacion = idPublicacion;
	}

	public int getCantServ() {
		return cantServ;
	}

	public void setCantServ(int cantServ) {
		this.cantServ = cantServ;
	}

	public int getCantAct() {
		return cantAct;
	}

	public void setCantAct(int cantAct) {
		this.cantAct = cantAct;
	}

	public int getCantNoches() {
		return cantNoches;
	}

	public void setCantNoches(int cantNoches) {
		this.cantNoches = cantNoches;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public LocalDate getFechaEgreso() {
		return fechaEgreso;
	}

	public void setFechaEgreso(LocalDate fechaEgreso) {
		this.fechaEgreso = fechaEgreso;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Tipo_Habitacion getTipoHabitacion() {
		return tipoHabitacion;
	}

	public void setTipoHabitacion(Tipo_Habitacion tipoHabitacion) {
		this.tipoHabitacion = tipoHabitacion;
	}

	public LinkedList<Servicios> getServicios() {
		return Servicios;
	}

	public void setServicios(LinkedList<Servicios> servicios) {
		Servicios = servicios;
	}

	public LinkedList<Actividad> getActividades() {
		return actividades;
	}

	public void setActividades(LinkedList<Actividad> actividades) {
		this.actividades = actividades;
	}
    
}