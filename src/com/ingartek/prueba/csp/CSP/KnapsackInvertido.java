package com.ingartek.prueba.csp.CSP;

import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.objective.ObjectiveStrategy;
import org.chocosolver.solver.objective.OptimizationPolicy;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

/**
 * 
 * <h1>KnapsackInvertido</h1>
 * <p>Inverted Knapsack algorithm. Based on {@link Knapsack}.</p>
 * 
 * @see <a href="https://github.com/chocoteam/choco3/blob/master/choco-samples/src/main/java/org/chocosolver/samples/integer/Knapsack.java">Wikipedia for Knapsack</a>
 * @see <a href="http://stackoverflow.com/questions/7939128/inverse-knapsack-issue">Stackoverflow Inverted Knapsack</a>
 * @see <a href="https://github.com/chocoteam/choco3/issues/319#issuecomment-132202259">My issue on Choco Solver<a/>
 * 
 * @author Jon Inazio
 *
 */
public class KnapsackInvertido extends AbstractProblem {

	/*
	 * Atributos
	 */
	// Constantes
	private Integer MAX_DOMAIN_VARS = 50;
	
	// Datos
	private Grupo grupo = new Grupo(14);
	private Vehiculo v1 = new Vehiculo(2, (float) 50);
	private Vehiculo v2 = new Vehiculo(7, (float) 71);
	private Vehiculo v3 = new Vehiculo(3, (float) 20);
	
	private IntVar[] variables = new IntVar[3];
	private int[] asientosVehiculos = new int[3];
	private int[] costesVehiculos = new int[3];
	
	private Integer sitiosServidos = 0;
	private Integer costeTotal = 0;
	
	// Variables
	private IntVar varCantidadVehiculos1;
	private IntVar varCantidadVehiculos2;
	private IntVar varCantidadVehiculos3;
	
	private IntVar varCoste;
	
	
	/*
	 * Métodos
	 */
	@Override
	public void createSolver() {
		 solver = new Solver("Knapsack Invertido");
	}

	@Override
	public void buildModel() {

		varCantidadVehiculos1 = VariableFactory.bounded("var1", 0, MAX_DOMAIN_VARS, solver);
		varCantidadVehiculos2 = VariableFactory.bounded("var2", 0, MAX_DOMAIN_VARS, solver);
		varCantidadVehiculos3 = VariableFactory.bounded("var3", 0, MAX_DOMAIN_VARS, solver);

		variables[0] = varCantidadVehiculos1;
		variables[1] = varCantidadVehiculos2;
		variables[2] = varCantidadVehiculos3;
		
		asientosVehiculos[0] = v1.getAsientos();
		asientosVehiculos[1] = v2.getAsientos();
		asientosVehiculos[2] = v3.getAsientos();
		
		costesVehiculos[0] = Math.round(v1.getCoste());
		costesVehiculos[1] = Math.round(v2.getCoste());
		costesVehiculos[2] = Math.round(v3.getCoste());
		
		varCoste = VariableFactory.bounded("coste", 0, 99999, solver);
		
		IntVar scalar = VariableFactory.fixed("ocupacion", grupo.getPersonas(), solver);
		
		solver.post(IntConstraintFactory.knapsack(variables, scalar, varCoste, asientosVehiculos, costesVehiculos));
		
	}

	@Override
	public void configureSearch() {
		@SuppressWarnings("rawtypes")
		AbstractStrategy strat = IntStrategyFactory.lexico_LB(new IntVar[]{varCantidadVehiculos1, varCantidadVehiculos2, varCantidadVehiculos3});
        solver.set(new ObjectiveStrategy(varCoste, OptimizationPolicy.BOTTOM_UP), strat);
        //Chatterbox.showDecisions(solver);
	}

	@Override
	public void solve() {
//		solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, varCoste);
		solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, varCoste, true);
	}

	@Override
	public void prettyOut() {
		StringBuilder st = new StringBuilder(String.format("Knapsack Invertido \n"));
		st.append(String.format("\tVehículo 1. Asientos=%d - Coste=%d - Cantidad=%d\n", v1.getAsientos().intValue(), v1.getCoste().intValue(), variables[0].getValue()));
		st.append(String.format("\tVehículo 2. Asientos=%d - Coste=%d - Cantidad=%d\n\n", v2.getAsientos().intValue(), v2.getCoste().intValue(), variables[1].getValue()));
		st.append(String.format("\tVehículo 3. Asientos=%d - Coste=%d - Cantidad=%d\n\n", v3.getAsientos().intValue(), v3.getCoste().intValue(), variables[2].getValue()));
		st.append(String.format("\tCoste total: %d", varCoste.getValue()));
	}

    public static void main(String[] args) {
        new KnapsackInvertido().execute();
    }

}
