package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.sa;



import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.AConstrutiveSolutionBuilder;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.sa.SimulatedAnnealingMath;

public class SimmulatedAnnealingTemperature {
    
    private ResultWriter out;
    

    public SimmulatedAnnealingTemperature(ResultWriter out){
        this.out = out;
    }
    
   
    public int[] execute(ModuleDependencyGraph mdg, AConstrutiveSolutionBuilder solutionBuilder, float coolingRate, float initialTemperatureRatio, int iterations){
        int currentIteration = 0;
        int[] solution = solutionBuilder.createSolution(mdg);
        ClusterMetrics cm = new ClusterMetrics(mdg, solution);// Controlador da solução - passa a solução inicial
        double currentCost = cm.calculateMQ(); //custo da solução atual
        double temperature=  SimulatedAnnealingMath.calculateInitialTemperature(currentCost, initialTemperatureRatio, 0.5d);
        writeIterationReport(solutionBuilder, coolingRate, initialTemperatureRatio, currentIteration, temperature);
        
        do{
            currentIteration++;
            temperature *= coolingRate;
            writeIterationReport(solutionBuilder, coolingRate, initialTemperatureRatio, currentIteration, temperature);
        }while(currentIteration < iterations);
        return null;
    }
    
    private void writeIterationReport(AConstrutiveSolutionBuilder solutionBuilder, float coolingRate, float initialTemperatureRatio, int iteration, double temperarure){
        out.writeLine(solutionBuilder.getName(), coolingRate+"", initialTemperatureRatio+"", iteration+"", temperarure+"");
    }
    
    
}

