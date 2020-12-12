package br.com.ppgi.unirio.marlon.smc.solution.algorithm.deterministic;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicAllModuleInSameClusterSolution;

public class DeterministicSearch {
	
	private static final String NAME="DET";
	
	private ResultWriter out;
	private String suffix;
	
	public DeterministicSearch(ResultWriter out, String suffix){
		this.out = out;
		this.suffix = suffix;
	}

	public int[] execute (ModuleDependencyGraph mdg){
        int[] solution = new ConstrutiveBasicAllModuleInSameClusterSolution().createSolution(mdg);//0-0-0-0-0-......
        long startTime = System.currentTimeMillis();
        ClusterMetrics cm = new ClusterMetrics(mdg, solution);
        int n = solution.length;

        long currentIteration = 0;
        
        int[] bestSolution = cm.cloneSolution();
        double bestCost = cm.calculateMQ();
        long bestSolutionIteration = 0;
        
        boolean hasNextMoviment;
        do{
            hasNextMoviment = incrementSolution(cm, 0);
            currentIteration++;
            
            if(cm.calculateMQ() > bestCost){
                bestSolutionIteration = currentIteration;
                bestCost = cm.calculateMQ();
                bestSolution = cm.cloneSolution();
            }
        }while(hasNextMoviment);
		out.writeLine(cm, bestCost, bestSolutionIteration, currentIteration, (System.currentTimeMillis() - startTime), NAME, "");
		return bestSolution;
	}
    
    private boolean incrementSolution(ClusterMetrics cm, int position){
        if(position >= cm.getMdg().getSize()){
            return false;
        }
        
        int[] solution = cm.getSolution();
        if(solution[position] < (cm.getMdg().getSize()-1)){
            cm.makeMoviment(position, solution[position]+1);
            return true;
        }else{
            if(incrementSolution(cm, position+1)){
                cm.makeMoviment(position, 0);
                return true;
            }
            return false;
        }
    }
}
