package Model;

import java.io.Serializable;

public class Ticket implements Serializable {
	String tipo;
	int cantidad;
	char action;
	String nombreCliente;
	String emailCliente;
	int gate;
	int row;
	int seat;
	double precio;
	String match;
	

	



	public Ticket(String tipo, int cantidad, char action, String nombreCliente, String emailCliente, int gate, int row,
			int seat, double precio, String match) {
		super();
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.action = action;
		this.nombreCliente = nombreCliente;
		this.emailCliente = emailCliente;
		this.gate = gate;
		this.row = row;
		this.seat = seat;
		this.precio = precio;
		this.match = match;
	}



	public String getMatch() {
		return match;
	}



	public void setMatch(String match) {
		this.match = match;
	}



	public int getGate() {
		return gate;
	}



	public void setGate(int gate) {
		this.gate = gate;
	}



	public int getRow() {
		return row;
	}



	public void setRow(int row) {
		this.row = row;
	}



	public int getSeat() {
		return seat;
	}



	public void setSeat(int seat) {
		this.seat = seat;
	}




	



	public Ticket(String tipo, int cantidad, char action, String match) {
		super();
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.action = action;
		this.match = match;
	}



	@Override
	public String toString() {
		return "Ticket [tipo=" + tipo + ", cantidad=" + cantidad + ", action=" + action + ", nombreCliente="
				+ nombreCliente + ", emailCliente=" + emailCliente + ", precio="
				+ precio + "]";
	}



	public String getTipo() {
		return tipo;
	}



	public void setTipo(String tipo) {
		this.tipo = tipo;
	}



	public int getCantidad() {
		return cantidad;
	}



	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}



	public char getAction() {
		return action;
	}



	public void setAction(char action) {
		this.action = action;
	}



	public String getNombreCliente() {
		return nombreCliente;
	}



	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}



	public String getEmailCliente() {
		return emailCliente;
	}



	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}



	public double getPrecio() {
		return precio;
	}



	public void setPrecio() {
		
		String tipo= this.tipo;
		
		switch (tipo) {
		case "tipo1": this.precio=10.00;
			break;
		case "tipo2": this.precio=20.00;
			break;
		case "tipo3": this.precio=30.00;
			break;
		
		}
	}



	public Ticket() {
		super();
	}
	
	
	
	
}
