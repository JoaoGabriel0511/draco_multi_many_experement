package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.hc;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicRandomSolution;
import random.number.generator.RandomWrapper;

/**
 * Implementação de um algoritmo de HillClimbing
 * @author kiko
 */
public class HillClimbing{
	
	private static final String NAME="HC";
	
	private ResultWriter out;
	private String suffix;
        private String params;
	
        public HillClimbing(ResultWriter out, String suffix){
            this(out, suffix, "");
        }
	
        public HillClimbing(ResultWriter out, String suffix, String params){
            this.out = out;
            this.suffix = suffix;
            this.params = params;
	}
        
        /**
         * Executa uma busca local até que um mínimo local seja obtido gerando soluções completamente aleatórias
         * @param mdg - Instância
         * @param solution - solução que será utilizada
         * @param threshold - Limite de vizinhos testados [0-100]
         * @return 
         */
        public int[] execute (ModuleDependencyGraph mdg, int[] solution, int threshold){
            ClusterMetrics cm = climbSolution(mdg, solution, threshold);
            return cm.getSolution();
        }
        
        /**
         * Executa uma busca local até que um mínimo local seja obtido gerando soluções completamente aleatórias
         * @param mdg - Instância
         * @param popSz - Quantidade de soluções aleatórias executadas
         * @param threshold - Limite de vizinhos testados [0-100]
         * @return 
         */
        public int[] execute (ModuleDependencyGraph mdg, int popSz, int threshold){
            int[] best = null;
            double maxMQ = Integer.MIN_VALUE;
            
            for(int i=0;i< popSz; i++){
                ClusterMetrics cm = climbSolution(mdg, new ConstrutiveBasicRandomSolution().createSolution(mdg), threshold);
                
                double currentPartitionMQ = cm.calculateMQ();
                if(currentPartitionMQ > maxMQ){//atualiza o melhor encontrado
                    best = cm.cloneSolution();
                    maxMQ = currentPartitionMQ;
                }
            }
            return best;
        }
        
        
        /**
         * Executa uma busca local até que um mínimo local seja obtido gerando soluções completamente aleatórias
         * @param mdg - Instância
         * @param solution - solução que será utilizada
         * @param threshold - Limite de vizinhos testados [0-100]
         * @return 
         */
        private ClusterMetrics climbSolution (ModuleDependencyGraph mdg, int[] solution, int threshold){
            long time = System.currentTimeMillis();
            ClusterMetrics cm = new ClusterMetrics(mdg, solution);
            out.writeLine(cm, cm.calculateMQ(), 0, 0, 0, NAME+suffix, params);
            int[] currentPartition = cm.cloneSolution();
            double currentPartitionMQ = cm.calculateMQ();


            climbHill(cm, threshold);//faz a escalada para um dos vizinhos
            int[] nextPartition = cm.cloneSolution();
            double nextPartitionMQ = cm.calculateMQ();

            while(nextPartitionMQ > currentPartitionMQ){//hill climbing funcionou
                currentPartition = nextPartition;
                currentPartitionMQ = nextPartitionMQ;

                climbHill(cm, threshold);//faz a escalada para um dos vizinhos
                nextPartition = cm.cloneSolution();
                nextPartitionMQ = cm.calculateMQ();
            }
            out.writeLine(cm, cm.calculateMQ(), 1, 1, System.currentTimeMillis()-time, NAME+suffix, params);
            return cm;           
        }
        
        private void climbHill(ClusterMetrics cm, int threshold){
            int neighborEvalLimit =(int) (cm.getMdg().getSize() * cm.getTotalClusteres() * (threshold/100.0d));
            double maxDelta = 0;
            int best_i = -1;
            int best_j = -1;
            
            int[] modulesMixed = RandomWrapper.createMixedArray(0,cm.getMdg().getSize()-1);
            int[] clusteresMixed = RandomWrapper.createMixedArray(0,cm.getTotalClusteres()-1);
            STOP_CLIMB:
            for(int i=0;i<cm.getMdg().getSize();i++){
                for(int j=0;j<cm.getTotalClusteres();j++){
                    //TODO - embaralhar a ordem
                    int moviment_i = modulesMixed[i];
                    int moviment_j = clusteresMixed[j];
                    
                    int clusterPosition = cm.convertToClusterNumber(moviment_j);
                    double currentDelta = cm.calculateMovimentDelta(moviment_i, clusterPosition);
                    
                    if(currentDelta > maxDelta){//vizinho é melhor
                        maxDelta = currentDelta;
                        best_i = moviment_i;
                        best_j = moviment_j;
                    }
                    
                    int visitedCount = (i*cm.getTotalClusteres()) + (j+1);
                    
                    if ( visitedCount >= neighborEvalLimit && maxDelta > 0 ){
                        break STOP_CLIMB;
                    }
                }
            }
            
            if(maxDelta > 0){
                int clustetPosition = cm.convertToClusterNumber(best_j);
                cm.makeMoviment(best_i, clustetPosition);
            }
        }
        
        

}
