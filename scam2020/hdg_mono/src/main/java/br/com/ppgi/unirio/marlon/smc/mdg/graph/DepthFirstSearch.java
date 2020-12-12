/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.mdg.graph;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marlon Monçores
 */
public class DepthFirstSearch {
    
    
    public static Map<String,List<Integer>> doSearch(ModuleDependencyGraph mdg){
        List<Integer> componentHead = new ArrayList<>();//primeiro elemento adicionado na componente
        List<Integer> componentSize = new ArrayList<>();//tamanho da componente
        
        Map<String,List<Integer>> returnData = new HashMap<>();
        returnData.put("HEAD", componentHead);
        returnData.put("SIZE", componentSize);
        
        boolean[] visited = new boolean[mdg.getSize()];       
        for(int i=0;i<visited.length;i++){
            visited[i] = false;
        }
        
        int lastVisited=0;
        do{
            for(;lastVisited<mdg.getSize();lastVisited++){//descobrir o primeiro elemento não visitado ainda
                if(!visited[lastVisited]){
                    break;//sai do for...
                }
            }
            if(lastVisited == mdg.getSize()){//todos os elementos foram visitados
                break;//sai do while
            }
            
            componentHead.add(lastVisited);
            Integer size = dfs(visited, mdg, lastVisited);
            componentSize.add(size);
        }while(true);
        
        return returnData;
    }
    
    private static int dfs(boolean[] visited, ModuleDependencyGraph mdg, int currentModule){
        visited[currentModule] = true;//módulo visitado
        int totalVisited = 1;
        for (int i = 0;i<mdg.getSize();i++){
            if(!visited[i] && mdg.checkHasDependency(currentModule, i)){
                totalVisited += dfs(visited, mdg, i);
            }
        }
        return totalVisited;
        
    }
}
