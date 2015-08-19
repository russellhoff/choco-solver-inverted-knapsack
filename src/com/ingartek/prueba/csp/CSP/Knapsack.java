package com.ingartek.prueba.csp.CSP;


import org.chocosolver.samples.AbstractProblem;
import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.objective.ObjectiveStrategy;
import org.chocosolver.solver.objective.OptimizationPolicy;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;
import org.kohsuke.args4j.Option;

/**
 * <a href="http://en.wikipedia.org/wiki/Knapsack_problem">wikipedia</a>:<br/>
 * "Given a set of items, each with a weight and a value,
 * determine the count of each item to include in a collection so that
 * the total weight is less than or equal to a given limit and the total value is as large as possible.
 * It derives its name from the problem faced by someone who is constrained by a fixed-size knapsack
 * and must fill it with the most useful items."
 * <p/>
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 31/03/11
 */
public class Knapsack extends AbstractProblem {

    @Option(name = "-d", aliases = "--data", usage = "Knapsack data ID.", required = false)
    Data data = Data.k20;

    @Option(name = "-n", usage = "Restricted to n objects.", required = false)
    int n = 13;

    // input data
    int[] capacites;
    int[] energies;
    int[] volumes;
    int[] nbOmax;

    // variables
    public IntVar power;
    public IntVar[] objects;


    public void setUp() {
        // read capacities
        capacites = new int[]{data.data[0], data.data[1]};
        int no = data.data[2];
        if (n > -1) {
            no = n;
        }
        energies = new int[no];
        volumes = new int[no];
        nbOmax = new int[no];
        for (int i = 0, j = 3; i < no; i++) {
            energies[i] = data.data[j++];
            volumes[i] = data.data[j++];
            nbOmax[i] = (int) Math.ceil(capacites[1] / volumes[i]);
        }
    }

    @Override
    public void createSolver() {
        solver = new Solver("Knapsack");
    }

    @Override
    public void buildModel() {
        setUp();
        int nos = energies.length;
        // occurrence of each item
        objects = new IntVar[nos];
        for (int i = 0; i < nos; i++) {
            objects[i] = VariableFactory.bounded("o_" + (i + 1), 0, nbOmax[i], solver);
        }
        // objective variable
        power = VariableFactory.bounded("power", 0, 9999, solver);

        IntVar scalar = VariableFactory.bounded("weight", capacites[0] - 1, capacites[1] + 1, solver);

        solver.post(IntConstraintFactory.knapsack(objects, scalar, power, volumes, energies));
    }

    @Override
    public void configureSearch() {
        AbstractStrategy<?> strat = IntStrategyFactory.lexico_LB(objects);
        // trick : top-down maximization
        solver.set(new ObjectiveStrategy(power, OptimizationPolicy.TOP_DOWN), strat);
        Chatterbox.showDecisions(solver);
    }

    @Override
    public void solve() {
        solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, power);
    }

    @Override
    public void prettyOut() {
        StringBuilder st = new StringBuilder(String.format("Knapsack -- %s\n", data.name()));
        st.append("\tItem: Count\n");
        for (int i = 0; i < objects.length; i++) {
            st.append(String.format("\t#%d: %d\n", i, objects[i].getValue()));
        }
        st.append(String.format("\n\tPower: %d", power.getValue()));
        System.out.println(st.toString());
    }

    public static void main(String[] args) {
        new Knapsack().execute(args);
    }


    ////////////////////////////////////////// DATA ////////////////////////////////////////////////////////////////////
    static enum Data {
        k10(new int[]{500, 550, 10,
                100, 79, 49, 25, 54, 99, 12, 41, 78, 94, 30, 75, 65, 40, 31, 59, 90, 95, 50, 99}),
        k20(new int[]{1000, 1100, 20,
                54, 38, 12, 57, 47, 69, 33, 90, 30, 79, 65, 89, 56, 28, 57, 70, 91, 38, 88, 71,
                77, 46, 99, 41, 29, 49, 23, 43, 39, 36, 86, 68, 12, 92, 85, 33, 22, 84, 64, 90}),;
        final int[] data;

        Data(int[] data) {
            this.data = data;
        }

        public int get(int i) {
            return data[i];
        }
    }
}