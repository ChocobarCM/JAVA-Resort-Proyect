package Entities;

public class Habitacion {
	    private int idHab;
	    private String num;
	    private int piso;
	    private String estado;
	    private Tipo_Habitacion tipo_Habitacion;
		public int getIdHab() {
			return idHab;
		}
		public void setIdHab(int idHab) {
			this.idHab = idHab;
		}
		public String getNum() {
			return num;
		}
		public void setNum(String num) {
			this.num = num;
		}
		public int getPiso() {
			return piso;
		}
		public void setPiso(int piso) {
			this.piso = piso;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public Tipo_Habitacion getTipo_Habitacion() {
			return tipo_Habitacion;
		}
		public void setTipo_Habitacion(Tipo_Habitacion tipo_Habitacion) {
			this.tipo_Habitacion = tipo_Habitacion;
		}
	    
		}


