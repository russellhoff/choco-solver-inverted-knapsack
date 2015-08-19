package com.ingartek.prueba.csp.CSP;

import java.util.UUID;

public class Grupo {

	private UUID id;
	private Integer personas;
	
	public Grupo(Integer pPersonas){
		id = UUID.randomUUID();
		personas = pPersonas;
	}

	public Integer getPersonas() {
		return personas;
	}

	public void setPersonas(Integer personas) {
		this.personas = personas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((personas == null) ? 0 : personas.hashCode());
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
		Grupo other = (Grupo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (personas == null) {
			if (other.personas != null)
				return false;
		} else if (!personas.equals(other.personas))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Grupo [" + (id != null ? "id=" + id + ", " : "") + (personas != null ? "personas=" + personas : "")
				+ "]";
	}
	
}
