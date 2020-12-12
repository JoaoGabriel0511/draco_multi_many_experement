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
public class DestrutiveMFMax extends ADestroySolution{

    public DestrutiveMFMax(LNSConfiguration config) {
        super("DMFMax", config);
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
            double highestMFValue = Integer.MIN_VALUE;
            int highestMFCluster = -1;
            for (int i=0;i<cm.getTotalClusteres();i++){
                int currentCluster = cm.convertToClusterNumber(i);
                double currentMF = cm.readClusterMF(currentCluster);

                if(currentMF > highestMFValue){
                    highestMFValue = currentMF;
                    highestMFCluster = currentCluster;
                }
            }
            //destruir todos os módulos do cluster escolhido
            while(cm.getModulesOnCluster(highestMFCluster).size() > 0 ){
                cm.makeMoviment(cm.getModulesOnCluster(highestMFCluster).get(0), -1);
                modulesLeft--;//modulo removido
                stopLoop = false;
            }
        }
    }
    
}
