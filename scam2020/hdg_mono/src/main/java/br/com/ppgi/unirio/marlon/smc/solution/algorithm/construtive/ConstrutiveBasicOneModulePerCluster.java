package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

public class ConstrutiveBasicOneModulePerCluster extends AConstrutiveSolutionBuilder{

    @Override
    public String getName() {
        return "CBOneModulePerCluster";
    }

    /**
     * Cria uma solução onde cada módulo está em um cluster
     * @param mdg
     * @return
     */
    @Override
    public int[] createSolution(ModuleDependencyGraph mdg) {
        int[] solution = new int[mdg.getSize()];

        for(int index=0;index<mdg.getSize();index++){
                solution[index] = index;
        }
        return solution;
    }

    @Override
    public int[][] createSolution(ModuleDependencyGraph mdg, int quantity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
