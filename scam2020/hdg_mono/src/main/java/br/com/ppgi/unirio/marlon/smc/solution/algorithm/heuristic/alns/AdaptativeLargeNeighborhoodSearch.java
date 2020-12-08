package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.alns;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import static br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveAglomerativeMQ.NAME;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LargeNeighborhoodSearch;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.sa.SimulatedAnnealingMath;
import random.number.generator.RandomWrapper;

public class AdaptativeLargeNeighborhoodSearch extends LargeNeighborhoodSearch{
    
    
    protected ALNSConfiguration config;
    private MethodsManager methodsManager;//componentes utilizados pela busca
    
    public AdaptativeLargeNeighborhoodSearch (ALNSConfiguration config){
        super(config);
        this.config = config;
        methodsManager = new MethodsManager(config);
    }
    
    @Override
    public String name(){
        return "ALNS";
    }
  
    
    
 
    
   
   
	
   
    
    /**
     * Executa a busca e retorna a melhor solução encontrada
     * @param mdg
     * @param solution
     * @return 
     */
    @Override
    protected int[] execute(ModuleDependencyGraph mdg, int[] solution){
        final long startTime = System.currentTimeMillis();//tempo inicial da execução
        
        final int n = solution.length;
        
        ClusterMetrics cm = new ClusterMetrics(mdg, solution);// Controlador da solução - passa a solução inicial
        double currentCost = cm.calculateMQ(); //custo da solução atual
        
        //estado da melhor solução
        int[] bestSolution = cm.cloneSolution();//best solution found
        config.setBestSolution(cm);
        double bestCost = currentCost;//best solution metric
        long bestSolutionIteration = 0; //iteração onde ocorreu a melhor solução

        //Controles internos da busca
        long currentIteration = 0;// current iteration
        //long iterationsWithoutImprovement = 0; //itrações sem melhoria       
        long biggestNoImprovementGap = 0;
        long timeElapsed = System.currentTimeMillis() - startTime;//tempo que a busca está rodando
        
        config.writeIterationReport(cm, bestCost, bestSolutionIteration, currentIteration, timeElapsed, NAME);
        
        //calcular temperatura inicial do algoritmo
        double temperarure = SimulatedAnnealingMath.calculateInitialTemperature(bestCost, config.getInicialTemperatureRatio(), 0.5d);
        
        do{
            currentIteration++;
            
            ClusterMetrics cmTemp = destroyAndRepairSolution(cm);
            double weight = config.getWeightWTCNA();//solução pior e não aceita
            if(accept(bestCost, cmTemp,temperarure)){
                cm = cmTemp;
                double readMQ = cm.calculateMQ();
                if(readMQ < currentCost){
                    weight = config.getWeightWTCA();//solução pior que a atual e aceita
                }else{
                    weight = config.getWeightBTC();//solução melhor que a atual
                }
                currentCost = readMQ;
            }
            
            if(currentCost > bestCost){
                weight = config.getWeightBSF();//solução é a melhor encontrada até o momento
                bestSolution = cm.cloneSolution();
                bestCost = currentCost;
                bestSolutionIteration = currentIteration;
//                iterationsWithoutImprovement = 0;
                config.setBestSolution(cm);
            }else{
  //              iterationsWithoutImprovement++;
            }
            
            long currentNoImprovementGap = currentIteration - bestSolutionIteration;
            if(currentNoImprovementGap > biggestNoImprovementGap){
                biggestNoImprovementGap=currentNoImprovementGap;
            }
            
            temperarure *= config.getCoolingRate();//diminui a temperatura
            
            methodsManager.updateLastExecutionWeight(weight);
            if(currentIteration % config.getRefreshScoreInterval() == 0){
                methodsManager.refreshScore();
            }
            config.writeIterationReport(cm, bestCost, bestSolutionIteration, currentIteration, timeElapsed, NAME);
        }while(canIterate(currentIteration, timeElapsed, biggestNoImprovementGap));

        return bestSolution;
    }
    
    /**
     * Esclolhe qual método de destruição e reparação será executado
     * @param cm
     * @return 
     */
    @Override
    protected ClusterMetrics destroyAndRepairSolution(ClusterMetrics cm){
        ClusterMetrics cm2 = cm.clone();
        methodsManager.destroyAndRepairSolution(cm2);
        return cm2;
    }
    
    
}

