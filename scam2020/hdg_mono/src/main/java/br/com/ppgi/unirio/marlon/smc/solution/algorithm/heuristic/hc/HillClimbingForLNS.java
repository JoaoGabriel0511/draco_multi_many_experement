package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.hc;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import random.number.generator.RandomWrapper;

/**
 * Implementação de um algoritmo de HillClimbing
 * @author kiko
 */
public class HillClimbingForLNS{
	
	
        /**
         * Executa uma busca local até que um mínimo local seja obtido, olhando pelo menos uma quantidade mínima de vizinhos
         * @param cm
         * @param threshold 
         */
        public static void execute (ClusterMetrics cm, float threshold){
            while(climbHill(cm, threshold));
        }
        
        /**
         * Troca a solução corrente para um de seus vizinhos que seja melhor que a solução corrente, retorna falso se não encontrar vizinho melhor
         * @param cm
         * @param threshold
         * @return 
         */
        private static boolean climbHill(ClusterMetrics cm, float threshold){
            int neighborEvalLimit =(int) (cm.getMdg().getSize() * cm.getTotalClusteres() * (threshold));
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
                return true;
            }
            return false;
        }
        
        

}
