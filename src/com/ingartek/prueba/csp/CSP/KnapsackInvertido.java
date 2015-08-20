package com.ingartek.prueba.csp.CSP;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.solution.ISolutionRecorder;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
//import org.chocosolver.util.ESat;

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
	private static final long serialVersionUID = -8561269826152490437L;
	private static Logger LOGGER = Logger.getLogger(KnapsackInvertido.class.getName());
	
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
	
	// Datos extra
	private Long cantidadSoluciones = 0L;
	
	/*
	 * Métodos
	 */
	public Long getCantidadSoluciones() {
		return cantidadSoluciones;
	}

	public void setCantidadSoluciones(Long cantidadSoluciones) {
		this.cantidadSoluciones = cantidadSoluciones;
	}

	@Override
	public void createSolver() {
		solver = new Solver("Knapsack Invertido");
		
		try {
			
			LocalDateTime ahora = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm"); 
			
			FileHandler fileHandler = new FileHandler("./knapsack" + ahora.format(formatter) + ".log");
			LOGGER.addHandler(fileHandler);
			fileHandler.setLevel(java.util.logging.Level.ALL);
			LOGGER.setLevel(java.util.logging.Level.ALL);
			LOGGER.config("Configuración del logger realizada.");


		} catch (SecurityException e) {
			LOGGER.severe("SecurityException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("IOException: " + e.getMessage());
			e.printStackTrace();
		}
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
		AbstractStrategy strat = IntStrategyFactory.lexico_LB(variables);
		// Esto no lo hacemos porque no queremos una búsqueda dicotómica. 
		//        solver.set(new ObjectiveStrategy(varCoste, OptimizationPolicy.BOTTOM_UP), strat);
        //Chatterbox.showDecisions(solver);
	}

	@Override
	public void solve() {
		solver.makeCompleteSearch(true);
		
		// Buscar una única solución óptima:
//		solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, varCoste);
		
		// Buscar todas las soluciones óptimas:
		solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, varCoste, false);
		
		// Buscar todas las soluciones:
//		cantidadSoluciones = solver.findAllSolutions();
		
		/*ESat feasible = solver.isFeasible();
		if(feasible.equals(ESat.TRUE)){
			LOGGER.info("feasible = TRUE");
		}else if(feasible.equals(ESat.FALSE)){
			LOGGER.info("feasible = FALSE");
		}else if(feasible.equals(ESat.UNDEFINED)){
			LOGGER.info("feasible = UNDEFINED");
		}
		
		ESat satisfied = solver.isSatisfied();
		if(satisfied.equals(ESat.TRUE)){
			LOGGER.info("satisfied = TRUE");
		}else if(satisfied.equals(ESat.FALSE)){
			LOGGER.info("satisfied = FALSE");
		}else if(satisfied.equals(ESat.UNDEFINED)){
			LOGGER.info("satisfied = UNDEFINED");
		}
//		Chatterbox.printAllFeatures(solver);*/
	}

	@Override
	public void prettyOut() {
		StringBuilder st = new StringBuilder(String.format("Knapsack Invertido \n"));
		st.append(String.format("\tVehículo 1. Asientos=%d - Coste=%d - Cantidad=%d\n", v1.getAsientos().intValue(), v1.getCoste().intValue(), varCantidadVehiculos1.getValue()));
		st.append(String.format("\tVehículo 2. Asientos=%d - Coste=%d - Cantidad=%d\n\n", v2.getAsientos().intValue(), v2.getCoste().intValue(), varCantidadVehiculos2.getValue()));
		st.append(String.format("\tVehículo 3. Asientos=%d - Coste=%d - Cantidad=%d\n\n", v3.getAsientos().intValue(), v3.getCoste().intValue(), varCantidadVehiculos3.getValue()));
		st.append(String.format("\tCoste total: %d", varCoste.getValue()));
		LOGGER.info(st.toString());
	}

    public static void main(String[] args) {
    	ejecutar();    	
    }

    public static void ejecutar(){
    	KnapsackInvertido problema = new KnapsackInvertido();
    	problema.execute();
    	problema.prettyOut();
    	Solver s = problema.getSolver();
    	ISolutionRecorder solRec = s.getSolutionRecorder();
    	List<Solution> soluciones = solRec.getSolutions();
    	
    	System.out.println("[*] Imprimiendo soluciones:");
    	for (Solution sol : soluciones) {
			System.out.println("- Solución: " + sol.toString() );
		}
    }
    
    public int getCantidadVehiculo1(){
    	return varCantidadVehiculos1.getValue();
    }

    public Float getCosteVehiculo1(){
    	return varCantidadVehiculos1.getValue()*v1.getCoste();
    }

    public int getCantidadVehiculo2(){
    	return varCantidadVehiculos2.getValue();
    }

    public Float getCosteVehiculo2(){
    	return varCantidadVehiculos2.getValue()*v2.getCoste();
    }

    public int getCantidadVehiculo3(){
    	return varCantidadVehiculos3.getValue();
    }

    public Float getCosteVehiculo3(){
    	return varCantidadVehiculos3.getValue()*v3.getCoste();
    }

    public int getCosteTotal(){
    	return varCoste.getValue();
    }
    
}
