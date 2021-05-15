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
public abstract class ARepairSolution {
    
    public final String NAME;
    protected final LNSConfiguration config;
    
    public ARepairSolution(String name,LNSConfiguration config){
        this.NAME = name;
        this.config = config;
    }
    
    public abstract void repair(ClusterMetrics cm);
    
}
