package com.ingartek.prueba.csp.CSP;

import java.util.UUID;

public class Vehiculo {

	private UUID id;
	private Integer asientos;
	private Integer asientosOcupados = 0;
	private Float coste;
	
	public Vehiculo(Integer pAsientos, Float pCoste){
		id = UUID.randomUUID();
		asientos = pAsientos;
		coste = pCoste;
	}

	public Integer getAsientos() {
		return asientos;
	}

	public void setAsientos(Integer asientos) {
		this.asientos = asientos;
	}

	public Float getCoste() {
		return coste;
	}

	public void setCoste(Float coste) {
		this.coste = coste;
	}

	public Integer getAsientosOcupados() {
		return asientosOcupados;
	}

	public void setAsientosOcupados(Integer asientosOcupados) {
		this.asientosOcupados = asientosOcupados;
	}
	
	public void aumentarAsientosOcupados(){
		this.asientosOcupados++;
	}

	public void disminuirAsientoOcupados(){
		this.asientosOcupados--;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asientos == null) ? 0 : asientos.hashCode());
		result = prime * result + ((asientosOcupados == null) ? 0 : asientosOcupados.hashCode());
		result = prime * result + ((coste == null) ? 0 : coste.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehiculo other = (Vehiculo) obj;
		if (asientos == null) {
			if (other.asientos != null)
				return false;
		} else if (!asientos.equals(other.asientos))
			return false;
		if (asientosOcupados == null) {
			if (other.asientosOcupados != null)
				return false;
		} else if (!asientosOcupados.equals(other.asientosOcupados))
			return false;
		if (coste == null) {
			if (other.coste != null)
				return false;
		} else if (!coste.equals(other.coste))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vehiculo [" + (id != null ? "id=" + id + ", " : "")
				+ (asientos != null ? "asientos=" + asientos + ", " : "")
				+ (asientosOcupados != null ? "asientosOcupados=" + asientosOcupados + ", " : "")
				+ (coste != null ? "coste=" + coste : "") + "]";
	}

}