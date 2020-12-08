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
public class DestrutiveMFMin extends ADestroySolution{

    public DestrutiveMFMin(LNSConfiguration config) {
        super("DMFMin", config);
    }

    @Override
    public void destroy(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int limit = (int) (mdgSize * config.getDestructionFactor());
        int modulesLeft = limit;
        boolean stopLoop = false;
        
        //destruir os clusteres com maior MF
         while(modulesLeft >=0 && !stopLoop){
            stopLoop = true;
            double lowestMFValue = Integer.MAX_VALUE;
            int lowestMFCluster = -1;
            for (int i=0;i<cm.getTotalClusteres();i++){
                int currentCluster = cm.convertToClusterNumber(i);
                double currentMF = cm.readClusterMF(currentCluster);

                if(currentMF < lowestMFValue){
                    lowestMFValue = currentMF;
                    lowestMFCluster = currentCluster;
                }
            }
            //destruir todos os módulos do cluster escolhido
            while(cm.getModulesOnCluster(lowestMFCluster).size() > 0 ){
                cm.makeMoviment(cm.getModulesOnCluster(lowestMFCluster).get(0), -1);
                modulesLeft--;//modulo removido
                stopLoop = false;
            }
        }
    }
    
}
