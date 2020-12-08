/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;

/**
 *
 * @author Marlon Monçores
 */
public class RepairGreedyWorstImprovement extends ARepairSolution{

    public RepairGreedyWorstImprovement(LNSConfiguration config) {
        super("RGWI", config);
    }
    
    protected RepairGreedyWorstImprovement(String name, LNSConfiguration config) {
        super(name, config);
    }

    @Override
    public void repair(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        
        boolean moved;
        do{
            //avaliar em qual cluster o módulo ficará melhor
            int selectedCluster = -1;
            int selectedModule = -1;
            double selectedDelta = Integer.MIN_VALUE;
            moved = false;
            
            for (int i=0;i<mdgSize;i++){//varrer os módulos na ordem
                int currentModule = i;
                int testCluster = cm.getSolution()[currentModule];
                if(testCluster != -1){
                    continue;//não precisa avaliar
                }
                
                //validar todos os clusteres
                for(int j=0;j<=cm.getTotalClusteres()&&cm.getTotalClusteres()<cm.getMdg().getSize();j++){//testa um cluster novo se puder criar
                    int currentCluster = cm.convertToClusterNumber(j);
                    double currentDelta = cm.calculateMovimentDelta(currentModule, currentCluster);
                    
                    if(
                            (selectedDelta < 0 && currentDelta > selectedDelta)//se delta for negativo tenta melhorar 
                            || 
                            (selectedDelta > 0 && currentDelta>0 && currentDelta < selectedDelta)//se for positivo, so aceita positivos piores
                        ){
                        selectedDelta = currentDelta;
                        selectedCluster = currentCluster;
                        selectedModule = currentModule;
                    }
                }
            }
            if(selectedCluster != -1){
                cm.makeMoviment(selectedModule, selectedCluster);//faz o movimento
                moved = true;
            }
        }while(moved);
    }
    
}
