Algoritmo Choco Solver (Constraint Satisfaction Problem Solver)
===================
Bienvenido al repositorio.  Aquí puedes encontrar todo lo necesario para determinar, dado un grupo de X personas y una serie de vehículos con Y asientos y coste por vehículo Z, la cantidad de vehículos de cada tipo necesarios, ya sea óptimamente o obteniendo todas las combinaciones posibles.

Para probar el programa, se puede lanzar el método main() de la clase Knapsack. También puede servir para aprender a lanzarlo:

    	Vehiculo v1 = new Vehiculo(2, (float) 50);
    	Vehiculo v2 = new Vehiculo(7, (float) 71);
    	Vehiculo v3 = new Vehiculo(3, (float) 20);
    	List<Vehiculo> vehics = new ArrayList<Vehiculo>();
    	vehics.add(v1);
    	vehics.add(v2);
    	vehics.add(v3);
    	
    	TipoSolucion sol = TipoSolucion.NormalSolution;
    	
    	ejecutar(14, vehics, sol);    	

Si se desea **probar** el programa desde otra clase java, se debería realizar lo siguiente:

 1. Crear un grupo de una determinada capacidad
 2. Crear una serie de vehículos, con su respectiva capacidad de asientos (sin tener en cuenta el conductor) y el coste asociado a su uso.
 3. Seleccionar un tipo de búsqueda a utilizar (Ver TipoSolucion).
 4. Ejecutar el método ejecutar() de la clase KnapsackInvertido, pasándole como parámetros los puntos 1, 2 y 3.
 5. Llamar al método getSoluciones() de KnapsackInvertido y hacer con las soluciones lo que sea necesario.
 
 