package com.ingartek.prueba.csp.CSP;

public enum TipoSolucion {
	
	/**
	 * Busca la solución óptima en función del coste del viaje con todos los vehículos. Elegir esta opción resultará en una única solución, ya que el algoritmo deshecha todas las demás salvo la óptima.
	 */
	OptimalSolution, 
	/**
	 * Método practicamente igual a {@link TipoSolucion#OptimalSolution}
	 */
	AllOptimalSolutions,
	/**
	 * Realiza una búsqueda de soluciones satisfaciendo las restricciones. El algoritmo no deshecha ninguna opción y almacena todas.
	 */
	NormalSolution;
}
