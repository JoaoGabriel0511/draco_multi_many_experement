/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

/**
 *
 * @author Marlon Monçores
 */
public class InitialSolutionFactory {
    public static enum CREATION_METHOD {
        CAMQ
    , CR
    };
    
    public static int totalKnownAlgorithms(){
        return CREATION_METHOD.values().length;
    }
    
    public static CREATION_METHOD getMethod(int algorithmNumber){
        if(algorithmNumber >= totalKnownAlgorithms()){
            return null;
        }
        return CREATION_METHOD.values()[algorithmNumber];
    }
    
    public static AConstrutiveSolutionBuilder build(CREATION_METHOD method){
        switch (method){
            case CAMQ: return new ConstrutiveAglomerativeMQ();
            case CR: return new ConstrutiveBasicRandomSolution();
        }
        throw new RuntimeException("ALGORITMO DESCONHECIDO!");
    }
}
