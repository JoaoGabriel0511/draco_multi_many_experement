package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.utils;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicOneModulePerCluster;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveBasicRandomSolution;
import java.util.ArrayList;
import random.number.generator.RandomWrapper;

public class TabuSearchSolutionsManager{ 
	
    private ArrayList<int[]> savedSolutions;
    
    public TabuSearchSolutionsManager(){
        savedSolutions = new ArrayList<>();
    }
    
    /**
     * Ordena os números dos módulos na solução
     * @param solution 
     */
    private void normalizeSolution(int[] solution){
        int[] modulesUsed = new int[solution.length];
        for(int i=0;i< modulesUsed.length;i++){
            modulesUsed[i] = -1;
        }
        
        int modulesCount = 0;
        for(int i=0;i< solution.length;i++){
            int module = solution[i];
            if(modulesUsed[module] == -1){
                modulesUsed[module] = modulesCount++;
            }
            solution[i] = modulesUsed[module];
        }
    }
 
    public boolean addSolution(int[] solution){
        normalizeSolution(solution);
        if(!checkSolutionExists(solution)){
            savedSolutions.add(solution);
            return true;
        }
        return false;
    }
    
    private boolean checkSolutionExists(int[] solution){
        NEXT_SOLUTION:
        for(int[] currentSolution : savedSolutions){
            for(int i=0;i< currentSolution.length;i++){
                if(currentSolution[i] != solution[i]){ //solucoes sao diferentes
                    continue NEXT_SOLUTION;
                }
            }
            //so chega aqui se forem totalmente iguais
            return true;
        }
        return false;
    }
    
    public int checkDifferenceBetweenSolutions (int[] solution1, int[] solution2, int currentLow){
        normalizeSolution(solution1);
        normalizeSolution(solution2);
        int diff = 0;
        for(int i=0;i<solution1.length;i++){
            if(solution1[i] != solution2[i]){
                diff++;
                if(currentLow != -1 && diff > currentLow){
                    return Integer.MAX_VALUE; //não foi verificado até o final
                }
            }
        }
        return diff;
    }
    
    private int[] generateBuildingBlockSolutions(){
        if(savedSolutions.size() == 0){
            return null;
        }
        
        
        int[] firstSolution = savedSolutions.get(0);
        int [] buildingBlock = new int[firstSolution.length];
        
        for(int i=1;i< savedSolutions.size();i++){
            int[] currentSolution = savedSolutions.get(i);
            for(int j=0;j<firstSolution.length;j++){
                buildingBlock[j] = (firstSolution[j] == currentSolution[j]) ? firstSolution[j] : -1 ; //seta o valor ou menos 1
            }
        }
        
        for(int i=0;i<buildingBlock.length;i++){
            if(buildingBlock[i] == -1){
                buildingBlock[i] = (int)RandomWrapper.unif(0, buildingBlock.length-1);
            }
        } 
        
        return buildingBlock;
    }
    
  
    public int[] generateNewSolution(ModuleDependencyGraph mdg){
        if(savedSolutions.size()==0){
            return new ConstrutiveBasicRandomSolution().createSolution(mdg);
        }
        int[] aux = generateBuildingBlockSolutions();
        savedSolutions = new ArrayList<>();
        return aux;
    }

}

