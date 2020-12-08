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
public class RepairRandom extends ARepairSolution{

    public RepairRandom(LNSConfiguration config) {
        super("RR", config);
    }
    
    protected RepairRandom(String name, LNSConfiguration config) {
        super(name, config);
    }

    @Override
    public void repair(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int[] checkOrder = RandomWrapper.createMixedArray(0,mdgSize-1);//verifica os módulos em uma ordem aleatória
        for (int i=0;i<mdgSize;i++){//para todos os módulos
            int currentModule = checkOrder[i];
            int testCluster = cm.getSolution()[currentModule];
            if(testCluster != -1){
                continue;//não precisa avaliar
            }
            cm.makeMoviment(currentModule, (int)RandomWrapper.unif(0, cm.getTotalClusteres()));//faz um movimento aleatório
        }
    }
    
}
