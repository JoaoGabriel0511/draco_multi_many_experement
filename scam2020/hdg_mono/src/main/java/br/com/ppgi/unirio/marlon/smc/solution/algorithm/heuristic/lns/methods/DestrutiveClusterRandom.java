/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;
import java.util.List;
import random.number.generator.RandomWrapper;

/**
 *
 * @author Marlon Monçores
 */
public class DestrutiveClusterRandom extends ADestroySolution{

    public DestrutiveClusterRandom(LNSConfiguration config) {
        super("DCR", config);
    }

    @Override
    public void destroy(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int[] destroyOrder = RandomWrapper.createMixedArray(0,cm.getTotalClusteres()-1);
        int limit = (int) (mdgSize * config.getDestructionFactor());
        int modulesLeft = limit;
        
        //destroi os clusteres em ordem aleatória
        for (int i=0;i< destroyOrder.length;i++){
            int currentCluster = cm.convertToClusterNumber(i);
            List<Integer> modulesOnCluster = cm.getModulesOnCluster(currentCluster);
            while(modulesOnCluster.size() > 0 && modulesLeft>0 ){
                int modulePositionToRemove = RandomWrapper.createRandomNumber(0, modulesOnCluster.size()-1, 1);
                cm.makeMoviment(modulesOnCluster.get(modulePositionToRemove), -1);
                modulesLeft--;//modulo removido
            }
            if(modulesLeft <= 0){
                break;
            }
        }
    }
    
}
