/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;
import random.number.generator.RandomWrapper;

/**
 *
 * @author Marlon Monçores
 */
public class RepairFirstImprovementRandom extends ARepairSolution{

    public RepairFirstImprovementRandom(LNSConfiguration config) {
        super("RFIR", config);
    }
    
    protected RepairFirstImprovementRandom(String name, LNSConfiguration config) {
        super(name, config);
    }

    @Override
    public void repair(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int[] checkOrder = RandomWrapper.createMixedArray(0,mdgSize-1);//verifica os módulos em uma ordem aleatória
        int auxClusterLimit = cm.getTotalClusteres()!=mdgSize ? cm.getTotalClusteres(): cm.getTotalClusteres()-1;
        int[] clustersOrder = RandomWrapper.createMixedArray(0,auxClusterLimit);
        
        NEXT_MODULE:
        for (int i=0;i<mdgSize;i++){//para todos os módulos
            int currentModule = checkOrder[i];
            int testCluster = cm.getSolution()[currentModule];
            if(testCluster != -1){
                continue;//não precisa avaliar
            }
                
            int selectedCluster = -1;
            double savedDelta = Integer.MIN_VALUE;
            
            
            
            for(int j=0;j< clustersOrder.length;j++){
                int currentCluster = cm.convertToClusterNumber(clustersOrder[j]);
                double currentDelta = cm.calculateMovimentDelta(currentModule, currentCluster);
                if(currentDelta > 0){
                    selectedCluster = currentCluster;
                    break;//não precisa mais procurar nos demais clusteres. Fará o movimento. primeira melhoria
                }else{//caso piore, gauradar os valores, pois se não houver melhora, o movimento será a melhor piora
                    if(currentDelta > savedDelta){
                        savedDelta = currentDelta;
                        selectedCluster = currentCluster;
                    }
                }
            }
            cm.makeMoviment(currentModule, selectedCluster);//faz o movimento
        }
    }
    
}
