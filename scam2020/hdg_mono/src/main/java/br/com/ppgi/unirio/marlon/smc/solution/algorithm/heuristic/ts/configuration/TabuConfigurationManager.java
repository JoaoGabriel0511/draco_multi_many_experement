package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.configuration;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.configuration.TabuConfiguration.TABU_TYPE;
import java.util.ArrayList;
import java.util.List;


public class TabuConfigurationManager {
    private static final int DEFAULT_ITERATIONS_LIMIT = 1000;//10000
    private static final int DEFAULT_PATH_RELINK = DEFAULT_ITERATIONS_LIMIT/100;
	
    private ResultWriter out;
    
    public TabuConfigurationManager(ResultWriter out){
        this.out = out;
    }
	
        
    public List<TabuConfiguration> createDefaultConfigurations(int n, int restarts){
        List<TabuConfiguration> configurations = new ArrayList<>();

        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_TWO_WAY,  n/8 ,n/4, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_TWO_WAY,  n/4 ,n/2, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_TWO_WAY,  n/2 ,n, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        
        
        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_SINGLE_WAY,  n/8 ,n/4, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_SINGLE_WAY,  n/4 ,n/2, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.MOVIMENT_SINGLE_WAY,  n/2 ,n, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        
        configurations.add(new TabuConfiguration(out, TABU_TYPE.LOCK_CLUSTER,  n/16 ,n/8, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.LOCK_CLUSTER,  n/8 ,n/4, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));
        configurations.add(new TabuConfiguration(out, TABU_TYPE.LOCK_CLUSTER,  n/4 ,n/2, DEFAULT_ITERATIONS_LIMIT+1, DEFAULT_ITERATIONS_LIMIT,DEFAULT_PATH_RELINK, restarts));

        return configurations;
    }
    
    
}
