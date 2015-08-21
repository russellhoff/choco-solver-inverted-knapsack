package com.ingartek.csp.knapsackinvertido;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.search.solution.ISolutionRecorder;
import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

/**
 * 
 * <h1>KnapsackInvertido</h1>
 * <p>Inverted Knapsack algorithm. Based on {@link Knapsack}.</p>
 * 
 * <p>
		Para ejecutar, hacer:
			<pre>
KnapsackInvertido problema = new KnapsackInvertido(Integer pTamanoGrupo, List<Vehiculo> pVehiculos, TipoSolucion pTipoSolucion);
problema.execute();
			</pre>

		Según lo que se le indique a {@link TipoSolucion}, el resultado será único o no.

		Para obtener los resultados, hay que llamar a la función {@link KnapsackInvertido#getSoluciones()}

 * </p>
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
	private static Logger LOGGER = Logger.getLogger(KnapsackInvertido.class.getName());
	
	// Constantes
	/**
	 * Valor máximo de cantidad de vehículos
	 */
	private Integer MAX_DOMAIN_VARS = 50;
	
	// Datos
	/**
	 * El grupo al que representa.
	 */
	private Grupo grupo = new Grupo(14);
	/**
	 * La lista de vehículos (mejor dicho, los tipos de vehículos).
	 */
	private List<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
	
	// Variables
	/**
	 * Un array de variables, que cada una contiene aquella cantidad de vehículos necesarios que se desean calcular.
	 */
	private IntVar[] variables = new IntVar[3];
	/**
	 * La cantidad de asientos de cada vehículo. Necesario para calcular con el algoritmo CSP de knapsack.
	 */
	private int[] asientosVehiculos = new int[3];
	/**
	 * Los costes de cada vehículo. También es necesario.
	 */
	private int[] costesVehiculos = new int[3];
	/**
	 * La variable de coste de CSP.
	 */
	private IntVar varCoste;
	
	// Datos extra
	/**
	 * Indica el tipo de solución que se va a usar el this.solve().
	 */
	private TipoSolucion tipoSolucion;
	/**
	 * Indica la cantidad de soluciones que se han almacenado en {@link KnapsackInvertido#soluciones}
	 */
	private Long cantidadSoluciones = 0L;
	/**
	 * Aquí es donde se almacenan las soluciones halladas. La lista de fuera indica el nivel de la solución (1ª solución, 2ª solución, 3ª solución, ... Nésima solución). 
	 * Dentro de esa lista hay un mapa con la cantidad necesaria para cada vehículo.
	 */
	private static List<Map<Vehiculo, Integer>> soluciones = new ArrayList<Map<Vehiculo, Integer>>();
	
	/*
	 * Métodos
	 */
	/**
	 * 
	 	<p>
	 
	 	Construye un objeto para realizar la búsqueda. Dependiendo de lo que se le indique a {@link TipoSolucion}, obtendremos diferentes resultados.
	 
	 	</p>
	 * 
	 * @param pTamanoGrupo El tamaño del grupo.
	 * @param pVehiculos La lista de los tipos de vehículos.
	 * @param pTipoSolucion El tipo de solución que se le va a dar esta vez. Ver {@link TipoSolucion}.
	 */
	public KnapsackInvertido(Integer pTamanoGrupo, List<Vehiculo> pVehiculos, TipoSolucion pTipoSolucion){
		grupo.setPersonas(pTamanoGrupo);
		vehiculos = pVehiculos;
		
		variables = new IntVar[pVehiculos.size()];
		asientosVehiculos = new int[pVehiculos.size()];
		costesVehiculos = new int[pVehiculos.size()];
		
		tipoSolucion = pTipoSolucion;
	}
	
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
			LOGGER.config("Configuraci�n del logger realizada.");

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

		IntVar auxVar;
		int i = 1;
		for(Vehiculo vehi : vehiculos){
			auxVar = VariableFactory.bounded("var"+i, 0, MAX_DOMAIN_VARS, solver);
			variables[i-1] = auxVar;
			asientosVehiculos[i-1] = vehi.getAsientos();
			costesVehiculos[i-1] = Math.round(vehi.getCoste());
			
			i++;
		}
		
		varCoste = VariableFactory.bounded("coste", 0, 99999, solver);
		
		IntVar scalar = VariableFactory.fixed("ocupacion", grupo.getPersonas(), solver);
		
		solver.post(IntConstraintFactory.knapsack(variables, scalar, varCoste, asientosVehiculos, costesVehiculos));
		
	}

	@Override
	public void configureSearch() {
		IntStrategyFactory.lexico_LB(variables);
	}

	@Override
	public void solve() {
		solver.makeCompleteSearch(true);
		
		if(tipoSolucion.equals(TipoSolucion.OptimalSolution)){
			solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, varCoste);
			int i = 0;
			do{
				
				for(int j = 0; j < variables.length; j++){
					addSolucion(i, vehiculos.get(j), variables[j].getValue());
				}
				
				i++;
				
			}while(solver.nextSolution());
			cantidadSoluciones = (long) (i+1);
			
		}else if(tipoSolucion.equals(TipoSolucion.AllOptimalSolutions)){
			solver.findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, varCoste, true);
			int i = 0;
			do{
				
				for(int j = 0; j < variables.length; j++){
					addSolucion(i, vehiculos.get(j), variables[j].getValue());
				}
				
				i++;
				
			}while(solver.nextSolution());
			cantidadSoluciones = (long) (i+1);
		}else{
			
			int i = 0;
			if(solver.findSolution()){
				do{
					
					for(int j = 0; j < variables.length; j++){
						addSolucion(i, vehiculos.get(j), variables[j].getValue());
					}
					
					i++;
					
				}while(solver.nextSolution());
			}	
			
			cantidadSoluciones = (long) (i+1);
			
		}
		
	}

	@Override
	public void prettyOut() {
		StringBuilder st = new StringBuilder(String.format("Knapsack Invertido \n"));
		
		int i = 0;
		for (Vehiculo vehi : vehiculos) {
			st.append(String.format("\tVehículo " + i + ". Asientos=%d - Coste=%d - Cantidad=%d\n", vehi.getAsientos().intValue(), vehi.getCoste().intValue(), variables[i].getValue()));
			
			i++;
		}
		
		st.append(String.format("\tCoste total: %d", varCoste.getValue()));
		LOGGER.info(st.toString());
		
	}

	/**
	 * 
	 * Este método es el Main, para ejecutar las pruebas. Modificar aquí el tamaño de grupo a calcular y los vehículos con sus capacidades y sus costes. Personalizar el tipo de búsqueda que se desea.
	 * 
	 * @param args No hay que pasarle nada como argumentos.
	 */
    public static void main(String[] args) {
    	Vehiculo v1 = new Vehiculo(2, (float) 50);
    	Vehiculo v2 = new Vehiculo(7, (float) 71);
    	Vehiculo v3 = new Vehiculo(3, (float) 20);
    	List<Vehiculo> vehics = new ArrayList<Vehiculo>();
    	vehics.add(v1);
    	vehics.add(v2);
    	vehics.add(v3);
    	
    	TipoSolucion sol = TipoSolucion.NormalSolution;
    	
    	ejecutar(14, vehics, sol);    	
    }

    /**
     * 
     * <p>Método al que se debe llamar si se desea realizar la búsqueda Knapsack. Se le debe facilitar los parámetros siguientes.</p>
     * 
     * <p>Si se desean obtener las <b>solucione(s)</b>, llamar al método {@link KnapsackInvertido#getSoluciones()}.</p>
     * 
     * @param pTamanoGrupo El tamaño del grupo.
     * @param pVehiculos Los vehículos.
     * @param pTipoSolucion El tipo de solución que se desea
     */
    public static void ejecutar(Integer pTamanoGrupo, List<Vehiculo> pVehiculos, TipoSolucion pTipoSolucion){
    	KnapsackInvertido problema = new KnapsackInvertido(pTamanoGrupo, pVehiculos, pTipoSolucion);
    	problema.execute();
    	problema.prettyOut();
    	Solver s = problema.getSolver();
    	ISolutionRecorder solRec = s.getSolutionRecorder();
    	List<Solution> sols = solRec.getSolutions();
    	
    	System.out.println("[*] Imprimiendo soluciones:");
    	for (Solution sol : sols) {
			System.out.println("- Solución: " + sol.toString() );
		}
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append("[*] Imprimiendo soluciones NUEVAS--\n");
    	for (int i = 0; i < soluciones.size(); i++) {
    		builder.append("\t" + i + " solución:\n");
			Map<Vehiculo, Integer> unaSolucion = soluciones.get(i);
			Iterator<Vehiculo> itVehiculos = unaSolucion.keySet().iterator();
			while(itVehiculos.hasNext()){
				Vehiculo vehi = itVehiculos.next();
				Integer valor = unaSolucion.get(vehi);
				builder.append("\t\tVehiculo con " + vehi.getAsientos() + " asientos: " + valor + "\n");
			}
		}
    	
    	LOGGER.info(builder.toString());
    }

    public int getCantidadVehiculoX(int i){
    	if(i < variables.length){
    		return variables[i].getValue();
    	}else{
    		return -1;
    	}
    	
    }

    /**
     * 
     * <p>Devuelve el coste del vehículo enésimo. Si no existe, devuelve -1.</p>
     * 
     * @param i
     * 
     * @return (Float) El coste del vehículo. Si no existe el vehículo, devuelve -1.
     *  
     */
    public Float getCosteVehiculoX(int i){
    	if(i < variables.length && i < vehiculos.size() ){
    		return variables[i].getValue()*vehiculos.get(i).getCoste();
    	}else{
    		return (float) -1;
    	}
    }

    public int getCosteTotal(){
    	return varCoste.getValue();
    }

	public List<Vehiculo> getVehiculos() {
		return vehiculos;
	}

	public void setVehiculos(List<Vehiculo> vehiculos) {
		this.vehiculos = vehiculos;
	}
    
	/**
	 * 
	 * <p>Método que sirve para añadir una solución a {@link KnapsackInvertido#soluciones}.</p>
	 * 
	 * @param pNivelSolucion El nivel de la solución (1º, 2º...)
	 * @param pVehiculo El vehículo asociado.
	 * @param pValor La cantidad de vehículos necesarios que indica esta solución.
	 */
	private void addSolucion(Integer pNivelSolucion, Vehiculo pVehiculo, Integer pValor){
		
		try{
			Map<Vehiculo, Integer> sols = soluciones.get(pNivelSolucion);
			sols.put(pVehiculo, pValor);
		}catch(IndexOutOfBoundsException e){
			Map<Vehiculo, Integer> sols = new HashMap<Vehiculo, Integer>();
			soluciones.add(pNivelSolucion, sols);
			sols.put(pVehiculo, pValor);
		}
		
	}
	
	/**
	 * 
	 * <p>Método que devuelve las soluciones obtenidas.</p>
	 * 
	 * @return List<Map<Vehiculo, Integer>> Las soluciones.
	 * 
	 */
	public List<Map<Vehiculo, Integer>> getSoluciones(){
		return soluciones;
	}
	
}
