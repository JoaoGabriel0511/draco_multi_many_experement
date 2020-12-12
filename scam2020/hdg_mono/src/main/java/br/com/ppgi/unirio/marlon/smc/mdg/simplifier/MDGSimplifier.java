/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.ppgi.unirio.marlon.smc.mdg.simplifier;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.util.ArrayList;
import java.util.List;

/**
 * Tenta diminuir o tamanho de um MDG seguindo a estrutura sugerida por
 * Mixed-Integer Linear Programming Formulations for the Software Clustering Problem
 * @author Marlon Monçores
 */
public class MDGSimplifier {
    
    private int sizeBefore;
    private int dependenciesBefore;
    private ModuleDependencyGraph mdg;
    private List<Integer> removedModules;
    
    private MDGSimplifier(ModuleDependencyGraph mdg){
        this.sizeBefore = mdg.getSize();
        this.dependenciesBefore = mdg.getTotalDependencyCount();
        this.removedModules = new ArrayList<>();
    }
    
    public static MDGSimplifier simplify(ModuleDependencyGraph mdg){
        MDGSimplifier returnData = new MDGSimplifier(mdg);
        returnData.mdg = mdg;
        int size = mdg.getSize();
        for(int i=0; i<size; i++){
            //verificar se o módulo só possui conexão com um outro módulo
            int uniqueDependency = mdg.getUniqueModuleDependency(i);
            if(uniqueDependency != -1){
                //efetuar o merge dos módulos
                mdg.insertModuleInsideAnother(uniqueDependency, i);
                returnData.removedModules.add((Integer)i);//grava o módulo que foi removido
                i--;//volta o contador, pois o módulo não existe mais
            }
        }
        
        returnData.mdg.refreshRemovedModules(returnData.removedModules);
        return returnData;
    }
    

    public int getSizeBefore() {
        return sizeBefore;
    }

    public void setSizeBefore(int sizeBefore) {
        this.sizeBefore = sizeBefore;
    }

    public int getDependenciesBefore() {
        return dependenciesBefore;
    }

    public void setDependenciesBefore(int dependenciesBefore) {
        this.dependenciesBefore = dependenciesBefore;
    }

    public ModuleDependencyGraph getMdg() {
        return mdg;
    }

    public void setMdg(ModuleDependencyGraph mdg) {
        this.mdg = mdg;
    }
    
    
    
}
