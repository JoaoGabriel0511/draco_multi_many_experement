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
public class DestrutiveMQMax extends ADestroySolution{

    public DestrutiveMQMax(LNSConfiguration config) {
        super("DMQMax", config);
    }

    @Override
    public void destroy(ClusterMetrics cm) {
        int mdgSize = cm.getMdg().getSize();
        int limit = (int) (mdgSize * config.getDestructionFactor());
        
        for(int i=0;i<limit;i++){//para cada módulo a ser removido...
            double separetedDelta = Integer.MIN_VALUE;
            int separatedModule = -1;
            
            for(int currentModule=0;currentModule<mdgSize;currentModule++){//avaliar todos os módulos da solução, para descobrir o melhor movimento
                if(cm.getSolution()[currentModule] == -1){//módulo já foi removido
                    continue;
                }
                
                double currentDelta = cm.calculateMovimentDelta(currentModule, -1);
                if(currentDelta > separetedDelta){
                    separetedDelta = currentDelta;
                    separatedModule = currentModule;
                }
            }
            cm.makeMoviment(separatedModule, -1);
        }
    }
}
