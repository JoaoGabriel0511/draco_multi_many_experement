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
public class RepairGreedyImproveWorstMF extends ARepairSolution{

    public RepairGreedyImproveWorstMF(LNSConfiguration config) {
        super("RGIWMF", config);
    }
    
    protected RepairGreedyImproveWorstMF(String name, LNSConfiguration config) {
        super(name, config);
    }

    @Override
    public void repair(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        
        boolean moved;
        do{
            //avaliar em qual cluster o módulo ficará melhor
            int fixedCluster = cm.smallestClusterMFNumber();//movimento feito para o pior cluster;
            int selectedModule = -1;
            double selectedDelta = Integer.MIN_VALUE;
            moved = false;
            
            for (int i=0;i<mdgSize;i++){//varrer os módulos na ordem
                int currentModule = i;
                int testCluster = cm.getSolution()[currentModule];
                if(testCluster != -1){
                    continue;//não precisa avaliar
                }
                
                double currentDelta = cm.calculateMovimentDelta(currentModule, fixedCluster);
                    
                if(currentDelta > selectedDelta){
                    selectedDelta = currentDelta;
                    selectedModule = currentModule;
                }
            }
            if(selectedModule != -1){
                if(selectedDelta < 0 && cm.getTotalClusteres()<cm.getMdg().getSize()){//posso cirar um cluster novo?
                    int newCluster = cm.convertToClusterNumber(cm.getTotalClusteres());
                    cm.makeMoviment(selectedModule, newCluster);//coloca em um novo cluster
                }else{
                    cm.makeMoviment(selectedModule, fixedCluster);//faz o movimento
                }
                moved = true;
            }
        }while(moved);
    }
}
