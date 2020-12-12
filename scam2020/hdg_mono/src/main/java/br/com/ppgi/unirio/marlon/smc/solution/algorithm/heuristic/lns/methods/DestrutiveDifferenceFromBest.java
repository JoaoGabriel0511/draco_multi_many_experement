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
public class DestrutiveDifferenceFromBest extends ADestroySolution{

    public DestrutiveDifferenceFromBest(LNSConfiguration config) {
        super("DDFB", config);
    }

    @Override
    public void destroy(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int[] destroyOrder = RandomWrapper.createMixedArray(0,mdgSize-1);
        int limit = (int) (mdgSize * config.getDestructionFactor());
        int removesLeft = limit;
        
        //remover primeiro todos os módulos que são diferentes na solução atual, comparados com a solução corrente
        for(int i=0; i< mdgSize;i++){
            if(cm.getSolution()[i] != config.getBestSolution().getSolution()[i]){
                cm.makeMoviment(i, -1);
                removesLeft--;
                if(removesLeft<=0){
                    break;
                }
            }
        }
        
        while(removesLeft > 0){
            for (int i=0;i<mdgSize && removesLeft > 0;i++){
                if(cm.getSolution()[destroyOrder[i]] == -1){
                    continue;
                }
                cm.makeMoviment(destroyOrder[i], -1);//move os módulos para o cluster -1
                removesLeft--;
            }
        }
    }
    
}
