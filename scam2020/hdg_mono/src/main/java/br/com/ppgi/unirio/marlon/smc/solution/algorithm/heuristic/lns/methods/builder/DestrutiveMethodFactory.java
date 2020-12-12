/*
 * To change this license header: case choose License Headers in Project Properties.
 * To change this template file: case choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder;

import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ADestroySolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveClusterRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveDifferenceFromBest;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveRandom;

/**
 *
 * @author Marlon Monçores
 */
public class DestrutiveMethodFactory {
    public static enum DESTRUTIVE_METHOD {DDFB, DR, DCR
    //,DMQMax, DCS, DMFMax, DMFMin, DMQMin 
    };
    
    
    public static int totalKnownAlgorithms(){
        return DESTRUTIVE_METHOD.values().length;
    }
    
    public static DESTRUTIVE_METHOD getMethod(int algorithmNumber){
        if(algorithmNumber >= totalKnownAlgorithms()){
            return null;
        }
        return DESTRUTIVE_METHOD.values()[algorithmNumber];
    }
    
    public static ADestroySolution build(DESTRUTIVE_METHOD method, LNSConfiguration config){
        switch (method){
            case DDFB: return new DestrutiveDifferenceFromBest(config);
            case DR: return new DestrutiveRandom(config);
            case DCR: return new DestrutiveClusterRandom(config);
//            case DMQMax: return new DestrutiveMQMax(config);
//            case DCS: return new DestrutiveClusterSmallest(config);
//            case DMFMax: return new DestrutiveMFMax(config);
//            case DMFMin: return new DestrutiveMFMin(config);
//            case DMQMin: return new DestrutiveMQMin(config);
            
        }
        throw new RuntimeException("ALGORITMO DESCONHECIDO!");
    }
}
