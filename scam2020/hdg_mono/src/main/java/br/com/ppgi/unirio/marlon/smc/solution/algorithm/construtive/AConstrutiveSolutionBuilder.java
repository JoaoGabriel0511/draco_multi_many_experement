/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

/**
 *
 * @author Marlon Monçores
 */
public abstract class AConstrutiveSolutionBuilder {
    
    
    public abstract String getName();
    
    public abstract int[] createSolution(ModuleDependencyGraph mdg);
    
    public abstract int[][] createSolution(ModuleDependencyGraph mdg, int quantity);
    
    
}
