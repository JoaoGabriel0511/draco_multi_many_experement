/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder;

import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ARepairSolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairFirstImprovementRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairGreedyBestImprovement;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairGreedyBestImprovementRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairGreedyWorstImprovement;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairGreedyWorstImprovementRandom;

/**
 *
 * @author Marlon Monçores
 */
public class RepairMethodFactory {
    public static enum REPAIR_METHOD {
        RGBIR
        , RGWIR
        , RFIR
//        , RJAIOC
//        , RR
        
        //metodos lentos
        , RGBI
        , RGBWI
//        , RGIWMF
    };

    
    public static int totalKnownAlgorithms(){
        return REPAIR_METHOD.values().length;
    }
    
    public static REPAIR_METHOD getMethod(int algorithmNumber){
        if(algorithmNumber >= totalKnownAlgorithms()){
            return null;
        }
        return REPAIR_METHOD.values()[algorithmNumber];
    }
    
    public static ARepairSolution build(REPAIR_METHOD method, LNSConfiguration config){
        switch (method){
            case RGBIR: return new RepairGreedyBestImprovementRandom(config);
            case RGWIR: return new RepairGreedyWorstImprovementRandom(config);
            case RFIR: return new RepairFirstImprovementRandom(config);
//            case RJAIOC: return new RepairJoinAllInOneCluster(config);
//            case RR: return new RepairRandom(config);

                
            case RGBI : return new RepairGreedyBestImprovement(config);
            case RGBWI : return new RepairGreedyWorstImprovement(config);
//            case RGIWMF: return new RepairGreedyImproveWorstMF(config);
        }
        throw new RuntimeException("ALGORITMO DESCONHECIDO!");
    }
}
