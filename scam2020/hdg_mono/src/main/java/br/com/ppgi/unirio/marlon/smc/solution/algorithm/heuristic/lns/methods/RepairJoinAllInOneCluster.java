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
public class RepairJoinAllInOneCluster extends ARepairSolution{

    public RepairJoinAllInOneCluster(LNSConfiguration config) {
        super("RJAIOC", config);
    }
    
    protected RepairJoinAllInOneCluster(String name, LNSConfiguration config) {
        super(name, config);
    }

    @Override
    public void repair(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int targetCluster = cm.nextAvailableCluster();
        if(targetCluster == -1){//não há clusteres disponíveis
            targetCluster = cm.getSmallestCluster();//pega o cluster que possui o menor numero de módulos
        }
        for (int i=0;i<mdgSize;i++){//para todos os módulos
            int currentModule = i;
            int testCluster = cm.getSolution()[currentModule];
            if(testCluster != -1){
                continue;//não precisa avaliar
            }
            cm.makeMoviment(currentModule, targetCluster);//faz um movimento aleatório
        }
    }
    
}
