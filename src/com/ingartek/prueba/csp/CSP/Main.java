package com.ingartek.prueba.csp.CSP;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

@Deprecated
/**
 * 
 * <p>For testing purposes only.</p>
 * 
 * @author Jon Inazio
 *
 */
public class Main {

	private static Integer MAX_DOMAIN_VARS = 50;
	
	private static Grupo grupo = new Grupo(9);
	private static List<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
//	private static List<IntVar> variables = new ArrayList<IntVar>();
	private static IntVar var1;
	private static IntVar var2;
	
	public static void main(String[] args) {

        // Creamos los vehículos:
		Vehiculo v1 = new Vehiculo(2, (float) 50);
		Vehiculo v2 = new Vehiculo(7, (float) 71);
		
		vehiculos.add(v1);
		vehiculos.add(v2);
		
		// Empezamos creando el solver:
		Solver solver = new Solver("csp");
		
		// Creamos las variables:
//		int i = 0;
//		for (Vehiculo v : vehiculos) {
//			IntVar aux = VariableFactory.bounded("v"+i, 0, MAX_DOMAIN_VARS, solver);
//			variables.add(aux);
//			i++;
//		}
		var1 = VariableFactory.bounded("var1", 0, MAX_DOMAIN_VARS, solver);
		var2 = VariableFactory.bounded("var2", 0, MAX_DOMAIN_VARS, solver);
		IntVar[] variables = {var1, var2};
		
		int[] costesVariables = {Math.round(v1.getCoste()), Math.round(v2.getCoste())};
		
		// Definimos las constraints:
		
//		IntVar suma = VariableFactory.bounded("suma", grupo.getPersonas(), grupo.getPersonas()+25, solver);
//		solver.post(IntConstraintFactory.sum(variables, suma));
//		solver.post(IntConstraintFactory.arithm(suma, ">=", grupo.getPersonas()));
		IntVar pers = VariableFactory.fixed(grupo.getPersonas(), solver);
		solver.post(IntConstraintFactory.scalar(variables, costesVariables, ">=", pers));
//		IntVar minimo = VariableFactory.bounded("minimo", 0, 500, solver);
//		IntConstraintFactory.minimum(minimo, variables);
		
		// Estrategia de búsqueda:
		solver.set(IntStrategyFactory.lexico_LB(new IntVar[]{var1, var2}));
		
		// Buscar solución:
		solver.findAllSolutions();
		
		// Imprimir las estadísticas de búsqueda:
		Chatterbox.printStatistics(solver);
		
		Chatterbox.printSolutions(solver);
		
	}

}
