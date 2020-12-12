package br.com.ppgi.unirio.marlon.smc.mdg;

import java.util.ArrayList;
import java.util.List;


/**
 * Insância representada como um Module Dependency Graph - MDG
 * @author kiko
 */
public class ModuleDependencyGraph {

    private String name;
    private int size;
    private int [][] dependencyWeight;// peso das dependencias
    private int [][] dependencyCount; //Contagem das dependencias entre módulos

    private int [][] moduleDependencies; //primeiro indice é o modulo e o segundo são as dependencias.
    private int [] moduleDependenciesCount; // total de dependencias de cada módulo


    private List<String> moduleNames;

    private int totalDependencyCount;//conta todas as dependencias.
    private int totalDependencyEdgeCount;//conta as arestas existentes
    private boolean weighted = false;

    /**
     * Cria um novo ModuleDependencyGraph - MDG
     * @param moduleNames 
     */
    public ModuleDependencyGraph(List<String> moduleNames){
        this.moduleNames = moduleNames;
        int totalModules = moduleNames.size();
        this.size=totalModules;
        dependencyWeight = new int[totalModules][totalModules];
        dependencyCount = new int[totalModules][totalModules];
        moduleDependencies = new int[totalModules][totalModules];
        moduleDependenciesCount = new int[totalModules];

        for(int i=0;i<totalModules;i++){
            moduleDependenciesCount[i] = 0;
            for(int j=0;j<totalModules;j++){ 
                dependencyWeight[i][j] = 0;
                dependencyCount[i][j] = 0;
                moduleDependencies[i][j] = -1;
            }
        }
        totalDependencyCount = 0;
        totalDependencyEdgeCount = 0;
    }

    /**
     * Adiciona uma dependencia entre dois módulos pelo nome dos módulos
     * @param module
     * @param dependsOn
     * @param weight 
     */
    public int addModuleDependency(String module, String dependsOn, int weight){
        int modulePosition = findModulePosition(module);
        int dependsOnPosition = findModulePosition(dependsOn);
        return addModuleDependency(modulePosition, dependsOnPosition, weight);
    }

    /**
     * Adiciona uma dependencia entre dois módulos pela posição dos módulos com uma dependencia
     * @param module
     * @param dependsOn
     * @param weight 
     */
    public int addModuleDependency(int module, int dependsOn, int weight){
        return addModuleDependency(module, dependsOn, weight,1);
    }

    /**
     * Adiciona uma dependencia entre dois módulos pela posição dos módulos com a quantidade de arestas existentes
     * @param module
     * @param dependsOn
     * @param weight
     * @param qty 
     * @return  
     */
    public int addModuleDependency(int module, int dependsOn, int weight, int qty){
        if(module > dependsOn){
            return addModuleDependency(dependsOn, module, weight, qty);
        }
        /*
        if(dependencyCount[module][dependsOn] == 0 && module != dependsOn){//não existe dependencia e não é o próprio módulo
            moduleDependency[module][dependencyCount[module]++]=dependsOn;
        }*/
        if(dependencyCount[module][dependsOn] ==0 ){
            totalDependencyEdgeCount ++;//adicona mais uma dependencia nova
            moduleDependencies[module][moduleDependenciesCount[module]++] = dependsOn;
            if(module != dependsOn){
                moduleDependencies[dependsOn][moduleDependenciesCount[dependsOn]++] = module;
            }
        }
        dependencyCount[module][dependsOn] ++;// adiciona uma dependencia entre os módulos
        dependencyWeight[module][dependsOn] += weight;// adiciona o peso atual
        totalDependencyCount+= qty;//adiciona a dependencia no MDG
        return dependencyWeight[module][dependsOn];
    }

    public void removeModuleDependency(int module, int wasDependentOn){
        if(module > wasDependentOn){
            removeModuleDependency(wasDependentOn, module);
            return;
        }

        if(dependencyCount[module][wasDependentOn] > 0){
            dependencyWeight[module][wasDependentOn] = 0;
            boolean existsDependency = removeDependencieInfo(module, wasDependentOn);
            if(existsDependency && module != wasDependentOn){
                removeDependencieInfo(wasDependentOn, module);
            }
            
            int count = dependencyCount[module][wasDependentOn];
            dependencyCount[module][wasDependentOn] = 0;
            totalDependencyCount -= count;//remove o total de dependencias
            totalDependencyEdgeCount --;//remove a aresta como um todo
        }
    }

    /**
     * Remove da lista de dependencias a informação de dependencia entra dois módulos
     * @param module1
     * @param module2
     * @return 
     */
    private boolean removeDependencieInfo(int module1, int module2){
        boolean found = false;
        if(module1 != module2){    
            for(int i=0;i< moduleDependencies[module1].length;i++){
                if(found){
                    moduleDependencies[module1][i-1] = moduleDependencies[module1][i];
                    moduleDependencies[module1][i] = -1;
                }
                if(!found && moduleDependencies[module1][i] == module2){
                    found=true;
                }
            }
            if(found){
                moduleDependenciesCount[module1]--;
            }
        }
        return found;
    }



    /**
     * Força da dependencia entre dois modulos
     * @param module
     * @param otherModule
     * @return 
     */
    public int dependencyWeight(int module, int otherModule){
        if(module > otherModule){
            return dependencyWeight(otherModule, module);
        }
        return dependencyWeight[module][otherModule];
    }

    /**
     * Quantidade de dependencias entre os dois módulos
     * @param module
     * @param otherModule
     * @return 
     */
    public int dependencyCount(int module, int otherModule){
        if(module > otherModule){
            return dependencyCount(otherModule, module);
        }
        return dependencyCount[module][otherModule];
    }


    /**
     * Encontra a posição de um módulo pelo seu nome
     * @param moduleName
     * @return 
     */
    private int findModulePosition (String moduleName){
        int i=0;
        for(String currentModuleName: moduleNames){
            if(currentModuleName.equals(moduleName)){
                return i;
            }
            i++;
        }
        throw new RuntimeException("MODULE NOT FOUND");
    }

        
    /**
     * Retorna o unico módulo conectado ao módulo module, ou -1 caso não seja verdade
     * @param module
     * @return 
     */
    public int getUniqueModuleDependency(int module){
        if(dependencyCount[module][module] > 0){
            return -1;//módulo possui auto relacionamento. não pode ser transformado em outro
        }
        int connectedModule = -1;
        for(int i=0;i<dependencyCount.length;i++){
            if((i<module && dependencyCount[i][module]>0) || (i>module && dependencyCount[module][i]>0)){
                if(connectedModule != -1){
                    return -1;//encontrou dois módulos relacionados
                }
                connectedModule = i;
            }
        }
        return connectedModule;
    }   
    
    
    /**
     * Junta um módulo que pode ser simplificado dentro do outro
     * @param moduleBase
     * @param moduleToBeInserted 
     */
    public void insertModuleInsideAnother (int moduleBase, int moduleToBeInserted){
        int weight = dependencyWeight(moduleBase, moduleToBeInserted);
        int count = dependencyCount(moduleBase, moduleToBeInserted);
        removeModuleDependency(moduleBase, moduleToBeInserted);
        size--;
        addModuleDependency(moduleBase, moduleBase, weight,count);//modulo dependente de si...
        
        /* 
        for (int i=0;i< size;i++){
            if(checkHasDependency(moduleToBeInserted, i)){
                throw new RuntimeException("AINDA EXISTEM DEPENDENCIAS COM MODULO REMOVIDO!");
            }
        }
        */
    }
        
    
    public boolean checkHasDependency(int moduleA, int moduleB){
        if(moduleA > moduleB){
            return dependencyCount[moduleB][moduleA]>0;
        }
        return dependencyCount[moduleA][moduleB]>0;
    }
        
       
    
    public void refreshRemovedModules(List<Integer> removedModules){
        this.moduleNames = buildModulesNamesList(removedModules);
        int [][] newDependencyWeight = new int[size][size];
        int [][] newDependencyCount = new int[size][size];
        int [][] newModuleDependencies = new int[size][size];
        int [] newModuleDependenciesCount = new int[size];
        
        /*
        for (int removed : removedModules){
            for (int i=0;i< size;i++){
                if(checkHasDependency(removed, i)){
                    throw new RuntimeException("AINDA EXISTEM DEPENDENCIAS COM MODULO REMOVIDO!");
                }
            }
        }
        */
        
        int[] newModuleNumbers = new int[moduleDependenciesCount.length];
        int offset = 0;
        for(int i=0;i< newModuleNumbers.length;i++){
            if(removedModules.contains(i)){
                offset++;
                newModuleNumbers[i] = -1;
            }else{
                newModuleNumbers[i] = i-offset;
            }
        }
        
        int removedOffseti = 0;
        for (int i=0; i< size;i++){
            while(removedModules.size()>removedOffseti && removedModules.get(removedOffseti)<=i+removedOffseti){
                removedOffseti++;
            }

            int removedOffsetj = 0;
            for (int j=0; j< size;j++){
                while(removedModules.size()>removedOffsetj && removedModules.get(removedOffsetj)<=j+removedOffsetj){
                    removedOffsetj++;
                }
                
                newDependencyWeight[i][j] = dependencyWeight[i+removedOffseti][j+removedOffsetj];
                newDependencyCount[i][j] = dependencyCount[i+removedOffseti][j+removedOffsetj];
                
                int currentDependency = moduleDependencies[i+removedOffseti][j];
                if(currentDependency == -1){
                    newModuleDependencies[i][j] = -1;
                }else{
                    newModuleDependencies[i][j] = newModuleNumbers[currentDependency];
                    if(newModuleNumbers[currentDependency] == -1){
                        throw new RuntimeException("DEPENDENCIA COM MÓDULO INEXISTENTE");
                    }
                }
            }
            newModuleDependenciesCount[i] = moduleDependenciesCount[i+removedOffseti];
        }
        
        dependencyWeight = newDependencyWeight;
        dependencyCount = newDependencyCount;
        
        moduleDependencies = newModuleDependencies;
        moduleDependenciesCount = newModuleDependenciesCount;
    }
    
    
    
    private List<String> buildModulesNamesList(List<Integer> removedModules){
        List<String> names = new ArrayList<>();
        int removedOffset = 0;
        for(int i=0; i< this.moduleNames.size();i++){
            if(removedModules.size() > removedOffset){
                int currentRemoved = removedModules.get(removedOffset);
                if(currentRemoved == i){
                    removedOffset++;
                    continue;
                }
            }
            names.add(this.moduleNames.get(i));
        }
        if(this.size != names.size()){
            throw new RuntimeException("ERRO AO DEIXAR APENAS NOMES VÁLIDOS");
        }
        return names;
    }
        
        
        
        
        
        
        
        
    /**
     * Total de modulos
     * @return 
     */
    public int getSize() {
            return size;
    }

    /**
     * Lista com os modulos que possuem relacionamento com o modulo informado
     * @param module
     * @return 
     */
    public int[] moduleDependencies(int module) {
        return moduleDependencies[module];
    }

    /**
     * total de dependencias que o modulo informado possui
     * @param module
     * @return 
     */
    public int moduleDependenciesCount(int module) {
        return moduleDependenciesCount[module];
    }

   


    /**
     * Nome da instância
     * @param name 
     */
    public String getName() {
            return name;
    }


    public void setName(String name) {
            this.name = name;
    }

    /**
     * Quantidade de dependências na instância
     * @return 
     */
    public int getTotalDependencyCount() {
        return totalDependencyCount;
    }

    public int getTotalDependencyEdgeCount() {
        return totalDependencyEdgeCount;
    }
	
    
        

    public boolean isWeighted() {
        return weighted;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public List<String> getModuleNames() {
        return moduleNames;
    }
	
    
    
}
