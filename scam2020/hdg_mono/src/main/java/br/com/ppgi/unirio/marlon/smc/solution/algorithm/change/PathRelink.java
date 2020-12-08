/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ppgi.unirio.marlon.smc.solution.algorithm.change;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;

/**
 *
 * @author kiko
 */
public class PathRelink {
        
    /**
     * Transforma iterativamente a solucao base na solução target, retornando a melhor solução encontrada, caso não seja a target.
     * @param target
     * @param base
     * @return 
     */
    public static ClusterMetrics relinkSolution(int[] targetSolution, double targetMQ, ClusterMetrics baseAux){
        ClusterMetrics base = new ClusterMetrics(baseAux.getMdg(), baseAux.cloneSolution());//cria uma c�pia para ser alterada sem interferir em nada externo
        int[] bestSolution = null;
        double bestSolutionMQ = targetMQ;//considera o target como melhor mq inicialmente
        boolean targerHasClusterN;
        int clusterN=0;
        do{//para cada cluster existente
            targerHasClusterN = false;
            //verificar quais módulos estão neste clustes na solução target
            for(int moduleN = 0; moduleN < targetSolution.length;moduleN++){
                if(targetSolution[moduleN] == clusterN){//modulo esta no cluster corrente
                    targerHasClusterN = true;
                    if(base.getSolution()[moduleN] != clusterN){//modulo foi trocado de posição na solução de destino
                        base.makeMoviment(moduleN, clusterN);//coloca o módulo no cluster de destino
                    }
                }
            }
            
            //todos os módulos do clusterN estão novamente no cluster
            if(base.calculateMQ() > bestSolutionMQ){//solução de melhor MQ encontrada atravez do path relink
                //System.out.println("PATH RELINK IMPROVED SOLUTION");
                bestSolution = base.cloneSolution();
                bestSolutionMQ = base.calculateMQ();
            }
            clusterN++;//proximo cluster
        }while(targerHasClusterN);
        
        if(bestSolution != null){//Path relink melhorou a solução!
            return new ClusterMetrics(base.getMdg(), bestSolution);
        }
        return null;//path relink não melhorou a solução
    }
}
