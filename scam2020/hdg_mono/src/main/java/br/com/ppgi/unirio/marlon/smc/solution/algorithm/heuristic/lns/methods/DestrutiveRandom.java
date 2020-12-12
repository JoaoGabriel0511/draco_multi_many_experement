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
public class DestrutiveRandom extends ADestroySolution{

    public DestrutiveRandom(LNSConfiguration config) {
        super("DR", config);
    }

    @Override
    public void destroy(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int[] destroyOrder = RandomWrapper.createMixedArray(0,mdgSize-1);
        int limit = (int) (mdgSize * config.getDestructionFactor());
        for (int i=0;i<limit;i++){
            cm.makeMoviment(destroyOrder[i], -1);//move os módulos para o cluster -1
        }
    }
    
}
