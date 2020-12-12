package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import random.number.generator.RandomWrapper;

public class ConstrutiveBasicRandomSolution extends AConstrutiveSolutionBuilder{

    @Override
    public String getName() {
        return "CR";
    }

    /**
     * Cria uma solução aleatória
     * @param mdg
     * @return
     */
    @Override
    public int[] createSolution(ModuleDependencyGraph mdg) {
        int[] solution = new int[mdg.getSize()];

        for(int index=0;index<mdg.getSize();index++){
            solution[index] = (int)RandomWrapper.unif(0, mdg.getSize()-1);
        }
        return solution;
    }

    @Override
    public int[][] createSolution(ModuleDependencyGraph mdg, int quantity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
