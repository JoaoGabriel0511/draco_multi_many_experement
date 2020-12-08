/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.alns;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ADestroySolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ARepairSolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveClusterRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveClusterSmallest;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveDifferenceFromBest;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveMFMax;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveMFMin;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveMQMax;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveMQMin;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.DestrutiveRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairGreedyBestImprovementRandom;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairJoinAllInOneCluster;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.RepairRandom;
import java.util.ArrayList;
import java.util.List;
import random.number.generator.RandomWrapper;

/**
 *
 * @author Marlon Monçores
 */
public final class MethodsManager {
    
    /**
     * Armazena os métodos e seus respectivos valores atuais
     */
    private class MethodData{
        ADestroySolution destroyAlgorithm;
        ARepairSolution repairAlgorithm;
        long totalExecutionTime = 0; //tempo total das execuções somados.
        int runTimesCount = 0;// quantidade de vezes que o algoritmo foi executado.
        int weightCount = 0;// peso do algoritmo obtido com as execuções.
        float currentWeight = 1;// peso utilizado para calcular qual o algoritmo que será utilizado
        float currentProbability = 0;// probabilidade do método escolhido ser utilizado
        
        public MethodData(ADestroySolution algorithm){
            destroyAlgorithm = algorithm;
        }
        
        public MethodData(ARepairSolution algorithm){
            repairAlgorithm = algorithm;
        }
    }
    
    private ALNSConfiguration config;
    
    private List<MethodData> destroyAlgorithms;
    private List<MethodData> repairAlgorithms;
    
    private MethodData lastUsedDestroy;
    private MethodData lastUsedRepair;
    
    private float totalWeightDestroy = 0;//total de peso existente entre todos os algoritmos.
    private float totalWeightRepair = 0;//total de peso existente entre todos os algoritmos.
    
    
    public MethodsManager(ALNSConfiguration config){
        this.config = config;
        addDefaultMethods(config);
        refreshScore();
    }
    
    /**
     * Cria os métodos que serão utilizados durante os sorteios
     * @param config 
     */
    private void addDefaultMethods(ALNSConfiguration config){
        destroyAlgorithms = new ArrayList<>();
        destroyAlgorithms.add(new MethodData(new DestrutiveRandom(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveMFMax(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveMFMin(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveClusterRandom(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveClusterSmallest(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveMQMax(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveMQMin(config)));
        destroyAlgorithms.add(new MethodData(new DestrutiveDifferenceFromBest(config)));
        
        repairAlgorithms = new ArrayList<>();
        repairAlgorithms.add(new MethodData(new RepairGreedyBestImprovementRandom(config)));
        repairAlgorithms.add(new MethodData(new RepairRandom(config)));
        repairAlgorithms.add(new MethodData(new RepairJoinAllInOneCluster(config)));
    }
    
    
    /**
     * Retorna o algoritmo de destruição de solução em uma posição específica, sem guardar nenhum histórico
     * @param position
     * @return 
     */
    public ADestroySolution getDestroyAlgorithm(int position){
        return destroyAlgorithms.get(position).destroyAlgorithm;
    }
    
    /**
     * Retorna o algoritmo de reparação de solução em uma posição específica, sem guardar nenhum histórico
     * @param position
     * @return 
     */
    public ARepairSolution getRepairAlgorithm(int position){
        return repairAlgorithms.get(position).repairAlgorithm;
    }
    
    /**
     * Efetua a destruição e reparo da solução.
     * @param cm 
     */
    public void destroyAndRepairSolution(ClusterMetrics cm){
        //DESTROY
        lastUsedDestroy = selectDestroyMethod();
        long startTime = System.currentTimeMillis();
        lastUsedDestroy.destroyAlgorithm.destroy(cm);//executa o médoto
        long endTime = System.currentTimeMillis();
        updateMethodUsedInfo(lastUsedDestroy, endTime - startTime);
        
        //REPAIR
        lastUsedRepair = selectRepairMethod();
        startTime = System.currentTimeMillis();
        lastUsedRepair.repairAlgorithm.repair(cm);//executa o médoto
        endTime = System.currentTimeMillis();
        updateMethodUsedInfo(lastUsedRepair, endTime - startTime);
        
    }
    
    /**
     * Atualiza nos ultimos métodos utilizados a informação que a solução gerada foi aceita
     * @param weight
     */
    public void updateLastExecutionWeight(double weight){
        lastUsedDestroy.weightCount += weight;
        lastUsedRepair.weightCount += weight;
    }
    
    /**
     * Efetua o cálculo de distribuição dos valores de cada algoritmo
     */
    public void refreshScore(){
        totalWeightDestroy =0;
        for(MethodData method : destroyAlgorithms){
            totalWeightDestroy += refreshScore(method);
        }
        for(MethodData method : destroyAlgorithms){
            refreshProbability(method,totalWeightDestroy,destroyAlgorithms.size());
        }
        
        totalWeightRepair = 0;
        for(MethodData method : repairAlgorithms){
            totalWeightRepair += refreshScore(method);
        }
        for(MethodData method : repairAlgorithms){
            refreshProbability(method,totalWeightRepair, repairAlgorithms.size());
        }
    }
    
    /**
     * Calcula a nova pontuação do algoritmo
     * @param method 
     */
    private float refreshScore(MethodData method){
        float newWeight = 0f;
        if(method.runTimesCount > 0){
            newWeight = method.currentWeight*(1-config.getReactionFactor()) + config.getReactionFactor()*method.weightCount/(float)method.runTimesCount;
        }
        method.currentWeight = newWeight;
        method.weightCount = 0;
        method.runTimesCount = 0;
        method.totalExecutionTime = 0;

        return newWeight;
    }
    
    private float refreshProbability (MethodData method, float totalWeight, int totalMethods){
        float p;
        if(totalWeight > 0){
            p = method.currentWeight / totalWeight;
        }else{
            p = 1/ (float)totalMethods;
        }
        method.currentProbability = p;
        return p;
    }
    
    /**
     * Atualiza os dados da execução de um método.
     * @param method
     * @param timeElapsed 
     */
    private void updateMethodUsedInfo(MethodData method, long timeElapsed){
        method.runTimesCount++;
        method.totalExecutionTime++;
    }
    
    /**
     * Selectiona um método seguindo a lógica de roleta
     * @return 
     */
    private MethodData selectDestroyMethod(){
        return selectMethodRouletteWheel(destroyAlgorithms);
    }
    
    /**
     * Selectiona um método seguindo a lógica de roleta
     * @return 
     */
    private MethodData selectRepairMethod(){
        return selectMethodRouletteWheel(repairAlgorithms);
    }
    
    private MethodData selectMethodRouletteWheel(List<MethodData> methods){
        double rand = RandomWrapper.rando();
        double accumulated = 0d;
        for(MethodData method : methods){
            accumulated += method.currentProbability;
            if(rand <= accumulated){
                return method;
            }
        }
        
        //retorna o ultimo. Feito pois a divisão para float é imprecisa e o somatorio das probabilidade ficará entre [0,1[
        return methods.get(methods.size()-1);
    }
}
