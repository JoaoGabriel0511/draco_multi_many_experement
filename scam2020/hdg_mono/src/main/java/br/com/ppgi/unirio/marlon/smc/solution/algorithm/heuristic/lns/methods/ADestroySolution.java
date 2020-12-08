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
public abstract class ADestroySolution {
    
    public final String NAME;
    protected final LNSConfiguration config;
    protected float df;
    
    public ADestroySolution(String name,LNSConfiguration config){
        this.NAME = name;
        this.config = config;
        this.df=config.getDestructionFactor();
    }
    
    public abstract void destroy(ClusterMetrics cm);
    
    public void changeDestructionFactor(float df){
        this.df=df;
    }
    
}
