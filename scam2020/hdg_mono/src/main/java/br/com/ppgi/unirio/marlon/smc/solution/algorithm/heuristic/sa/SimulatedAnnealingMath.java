/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.sa;

/**
 *
 * @author Marlon Monçores
 */
public class SimulatedAnnealingMath {
    
    
    /**
     * Recebe um valor inicial, um percentual de variação e uma probabilidade.
     * Retorna um valor que é a tamperatura inicial de um simulated annealing onde
     * o initialValue * variation terá o factor possibilidade de ser sorteado
     * a fórmula base para o cálculo é e^(-(final-inicial)/T)
     * @param initialValue
     * @param variation - número entre 0 e 1
     * @param factor
     * @return 
     */
    public static double calculateInitialTemperature(double initialValue, double variation, double factor){
        double finalValue = initialValue * (1-variation);
        double delta = finalValue - initialValue;
        return (delta)/Math.log(factor);
    }
    
    
    public static double checkProbability(double initialValue, double finalValue, double temperature){
        double result = Math.pow(Math.E, ((finalValue-initialValue))/temperature);
        return result;
    }
}
