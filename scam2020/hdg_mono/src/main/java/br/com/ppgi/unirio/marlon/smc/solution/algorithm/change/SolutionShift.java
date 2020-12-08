/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ppgi.unirio.marlon.smc.solution.algorithm.change;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;

/**
 *
 * @author kiko
 */
public class SolutionShift {
        
    /**
     * Transforma a solu��o deslocando todos os elementos uma quantidade para o lado (modulo[i] = modulo[i+quantidade])...
     * @param target
     * @param base
     * @return 
     */
    public static ClusterMetrics ShiftSolution(ClusterMetrics solution, int offset){
        int solutionLengh = solution.getSolution().length;
        offset = offset % solutionLengh;//evitar desperdicio
        
        if(offset <= 0){ return solution;}//n�o farz nada
        
        int[] shiftedSolution = solution.cloneSolution();
        
        for(int i=0; i< offset;i++){
            int aux = shiftedSolution[0];//guarda o primeiro elemento
            for(int moduleN=1; moduleN< shiftedSolution.length;moduleN++){
                shiftedSolution[moduleN-1] = shiftedSolution[moduleN];
            }
            shiftedSolution[solutionLengh-1] = aux;
        }
        
        return new ClusterMetrics(solution.getMdg(),shiftedSolution);
        
    }
}
