package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.change.PathRelink;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicOneModulePerCluster;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicRandomSolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.configuration.TabuConfiguration;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.utils.TabuSearchSolutionsManager;
import random.number.generator.RandomWrapper;

public abstract class TabuSearch {
    
    protected ResultWriter out;
    protected String suffix;
    protected String NAME;
    protected TabuConfigurations config;
    protected TabuSearchSolutionsManager solutionManager;
    
    private class TabuConfigurations{
        double minTabuTime = (double) 9/10;//tempo que um procedimento ficará como tabu - margem mínima para sorteio
        double maxTabuTime = (double) 11/10;//tempo que um procedimento ficará como tabu - margem máxima para sorteio
        long aspirationInterval = 10;//marcador de quantas em quantas iterações ocorrerá uma aspiração automática
        long nrIterations = 1000;//quantidade de iterações que serão executadas
        int pathRelinkInterval = 100;//quando executar o pathrelink
        
        boolean multistart = true;//busca tabu será multistart?
        long restartInterval = 100;//quantidade de iterações para se efetuar o restart
        
        private void configure(TabuConfiguration configuration){
            this.minTabuTime = configuration.getMinSize();
            this.maxTabuTime = configuration.getMaxSize();
            this.aspirationInterval = configuration.getAspiration();
            this.nrIterations = configuration.getNrIterations();
            this.pathRelinkInterval = configuration.getPathRelinkInterval();
        }
        
        @Override
        public String toString(){
            return minTabuTime
                    +"_"+maxTabuTime
                    +"_"+aspirationInterval
                    ;
        }
        
    }
	
    public TabuSearch(ResultWriter out,String suffix){
        this.out = out;
        this.suffix = suffix;
        config = new TabuConfigurations();
        solutionManager = new TabuSearchSolutionsManager();
    }

    
    /**
     * Utiliza a solução informada como solução inicial e executa a busca tabu com as configurações definidas
     * @param mdg
     * @param solution
     * @param configuration
     * @return 
     */
    public int[] execute(ModuleDependencyGraph mdg, int[] solution, TabuConfiguration configuration){
        config.configure(configuration);
        return execute(mdg, solution);
    }
    
    /**
     * Gera uma solução aleeatória e executa a busca tabu com as configurações definidas
     * @param mdg
     * @param configuration
     * @return 
     */
    public int[] execute(ModuleDependencyGraph mdg, TabuConfiguration configuration){
        config.configure(configuration);
        
        int[] solution = new ConstrutiveBasicRandomSolution().createSolution(mdg);
        return execute(mdg, solution);
    }
    
   
    protected abstract void resetTabuList(int n);
    protected abstract long checkTabuValue(int module, int cluster);
    protected abstract void addTabu(long iterationN, int module, int cluster, int modulePositionBefore);
	
    
    
    
    private int[] execute(ModuleDependencyGraph mdg, int[] solution){
        //configuração da execução
        final long startTime = System.currentTimeMillis();
        final int n = solution.length;
        final long minTabuTime = (long) (config.minTabuTime * n);
        final long maxTabuTime = (long) (config.maxTabuTime * n);
        final long aspirationInterval = config.aspirationInterval * n;
        final long nrIterations = config.nrIterations;
        final int pathRelinkInterval = config.pathRelinkInterval;
        final boolean multistart = config.multistart;
        final long restartInterval = config.restartInterval;
        final String params = config.toString();

        //variaveis com o estado atual da busca
        long currentIteration = 1;               // current iteration
        long iterationsWithoutImprovement = 0; //itrações sem melhoria
        long iterationsToPathRelink = 0; //itrações sem melhoria

        int[] bestSolution = null;//best solution found
        double bestCost = Integer.MIN_VALUE;//best solution metric
        long bestSolutionIteration = 0; //iteração onde ocorreu a melhor solução

        double currentCost = 0;
        ClusterMetrics cm = null;            
        
        boolean clusterCreatedOnLastMove = false; //informação se no ultimo movimento foi criado um cluster
        int lastMovedCluster = -1; //ultim cluster movido
        double[][] delta = new double[n][n];

        // ------------- loop tabu ------------------
        for (; currentIteration <= (nrIterations); currentIteration++){
            if((currentIteration == 1) || (multistart && restartInterval <= iterationsWithoutImprovement)){//inicio ou reset da busca
                iterationsWithoutImprovement = 0;
                iterationsToPathRelink = 0;
                if (currentIteration != 0){//gerar outra solução
                    solution = solutionManager.generateNewSolution(mdg);
                }
                
                cm = new ClusterMetrics(mdg, solution);
                currentCost = cm.calculateMQ(); // current sol. value
                
                if(currentCost > bestCost){
                    solutionManager.addSolution(cm.cloneSolution());
                    bestSolution = cm.cloneSolution();
                    bestCost = currentCost; 
                }

                clusterCreatedOnLastMove = false; //informação se no ultimo movimento foi criado um cluster
                lastMovedCluster = -1; //ultim cluster movido
                
                delta = new double[n][n];

                // ----------------- preenche a lista tabu -----------------
                resetTabuList(n);
            }
            
            
//                        System.out.println(cm.getSolutionAsString());
            out.writeLine(cm, bestCost, bestSolutionIteration, currentIteration, (System.currentTimeMillis() - startTime), NAME, suffix+"_"+params);

            //verificar quais movimentos podem ser feitos
            int i_retained = Integer.MAX_VALUE;       // in case all moves are tabu
            int j_retained = -1;
            double maxDelta = Integer.MIN_VALUE;   // retained move cost
            boolean autorized;               // move not tabu?
            boolean aspired;                 // move forced?
            boolean already_aspired = false; // in case many moves forced

            //recupera o melhor movimento disponível...
            for (int auxi = 0; auxi < n; auxi++)//modulo
            {
                for (int auxj = 0; auxj < cm.getTotalClusteres(); auxj = auxj+1)//cluster
                {
                    int i=cm.convertToClusterNumber(auxi);
                    int j=cm.convertToClusterNumber(auxj);

                    if(solution[i] == j) {continue;}//modulo esta no cluster de destino
                    if(j == cm.getTotalClusteres() && cm.isModuleAlone(i)) {continue;} //modulo isolado sendo trocado de cluster, n�o altera a solu��o
                    if(clusterCreatedOnLastMove && i==lastMovedCluster && j==cm.getTotalClusteres()) {continue;}//modulo movido para um cluster novo na itera��o anterior

                    delta[i][j] = cm.calculateMovimentDelta(i, j);
                    autorized = (checkTabuValue(i, j) < currentIteration);
                    aspired = (checkTabuValue(i, j) < currentIteration-aspirationInterval) || (currentCost + delta[i][j] > bestCost); 

                    if 
                        (
                            (aspired && !already_aspired) || // first move aspired
                            (aspired && already_aspired &&    /* many move aspired */ (delta[i][j] > maxDelta) ) || // => take best one
                            (!aspired && !already_aspired &&  /* no move aspired yet*/ (delta[i][j] > maxDelta) && autorized)
                        )
                    {
                        i_retained = i;
                        j_retained = j;
                        maxDelta = delta[i][j];
                        if (aspired){already_aspired = true;}
                    }
                }
            }

            //processar o movimento
            if (i_retained == Integer.MAX_VALUE){
                System.out.println("All moves are tabu! \n"); 
            }
            else 
            {
                int modulePositionBefore = solution[i_retained]; //cluster onde o módulo está antes do movimento
                clusterCreatedOnLastMove = j_retained == cm.getTotalClusteres();
                lastMovedCluster = i_retained;
//                            System.out.println("ITERATION: "+currentIteration+" MOVIMENT: "+i_retained+" - "+ j_retained + " DELTA: "+delta[i_retained][j_retained]);
                if(delta[i_retained][j_retained] == 0){
                    System.out.print("");
                }
                cm.makeMoviment(i_retained, j_retained); //efetua o movimento

                currentCost = cm.calculateMQ(); //novo custo

                //proibir o movimento reverso e o mesmo movimento por um número aleatório de iterações
                long randomN = RandomWrapper.unif(minTabuTime, maxTabuTime);
                addTabu(randomN, i_retained, j_retained, modulePositionBefore);


                iterationsWithoutImprovement++;//incremeta iterações sem melhoria
                iterationsToPathRelink++;
                if (currentCost > bestCost)// melhor solução melhorou ?
                {
                    bestCost = currentCost;
                    bestSolution = cm.cloneSolution();
                    bestSolutionIteration = currentIteration;
                    iterationsWithoutImprovement = 0;
                    iterationsToPathRelink = 0;
                    solutionManager.addSolution(cm.cloneSolution());
                }

                //pathRelink?
                if(pathRelinkInterval > 0 && iterationsToPathRelink >= pathRelinkInterval){
                    ClusterMetrics pathRelinkCM = PathRelink.relinkSolution(bestSolution, bestCost, cm);
                    if(pathRelinkCM != null){//houve melhora na bestSolution!
                        bestCost = pathRelinkCM.calculateMQ();
                        bestSolution = pathRelinkCM.cloneSolution();
                        bestSolutionIteration = currentIteration;
                        cm = pathRelinkCM;
                        solution = cm.getSolution();
                        iterationsWithoutImprovement = 0;
                        solutionManager.addSolution(cm.cloneSolution());
                    }
                    iterationsToPathRelink = 0;
                }
            }
        }
        out.writeLine(cm, bestCost, bestSolutionIteration, currentIteration, (System.currentTimeMillis() - startTime), NAME, suffix+"_"+params);
        return bestSolution;
    }
}

