/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ppgi.unirio.marlon.smc.instance.graph;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

/**
 * Constroi um arquivo que pode ser utilizado com a ferramenta GraphViz para exibir um grafo
 * @author kiko
 */
public class GraphViz {
    
    /**
     * Cria o grafo para uma instância
     * @param mdg
     * @return 
     */
    public static StringBuilder generateGraphCode(ModuleDependencyGraph mdg){
        return generateGraphCode(mdg, null);
    }
    
    /**
     * Cria o grafo para uma instância, colorindo os vértices de acordo com seu respectivo cluster
     * @param mdg
     * @param solution
     * @return 
     */
    public static StringBuilder generateGraphCode(ModuleDependencyGraph mdg, int[] solution){
        StringBuilder sb = new StringBuilder();
        
        sb.append(createHeader(mdg));
        sb.append(createDependencyList(mdg));
        sb.append(formatNodes(mdg, solution));
        sb.append(createFooter(mdg));
        
        
        return sb;
    }
    
    private static StringBuilder createHeader(ModuleDependencyGraph mdg){
        StringBuilder sb = new StringBuilder();
        
        sb.append("digraph ");
        sb.append(mdg.getName());
        sb.append("{");
        sb.append('\n');
        
        return sb;
    }
    
    private static StringBuilder createDependencyList(ModuleDependencyGraph mdg){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i< mdg.getSize();i++){
            for (int j=i;j<mdg.getSize();j++){
                if(mdg.dependencyWeight(i, j) > 0){
                    sb.append(i);
                sb.append(" -> ");
                sb.append(j);
                sb.append(" [dir=none weight=").append(mdg.dependencyWeight(i, j)).append("]");
                sb.append('\n');
                }
            }
        }
        /*
        for (int module=0; module<mdg.getSize(); module++){
            int [] moduleDependency = mdg.getModuleDependency(module);
            for(int depN = 0; depN < mdg.getModuleDependencyCount(module); depN++){
                int dependency = moduleDependency[depN];
                sb.append(module);
                sb.append(" -> ");
                sb.append(dependency);
                sb.append('\n');
            }
        }*/
        
        return sb;
    }
    
    private static StringBuilder formatNodes(ModuleDependencyGraph mdg, int[] solution){
        StringBuilder sb = new StringBuilder();
        
        for (int module=0; module<mdg.getSize(); module++){
            sb.append(module);
            sb.append(" ");
            sb.append("[shape=ellipse,fillcolor=");
            if(solution == null){
                sb.append(calculateColor(0));
            }else{
                sb.append(calculateColor(solution[module]));
            }
            sb.append(",style=\"filled\"]");    
            sb.append('\n');
        }
        
        return sb;
    }
    
    private static StringBuilder createFooter(ModuleDependencyGraph mdg){
        StringBuilder sb = new StringBuilder();
        
        sb.append("}");
        sb.append('\n');
        
        return sb;
    }
 
    public static StringBuilder calculateColor(int color){
        String[] symbols = {"FF","DD","BB","88","44","33", "22","11", "00"};
        int colorLimit = symbols.length* symbols.length* symbols.length;
        if(color >= colorLimit){
            throw new RuntimeException("IMPOSSIVEL CRIAR ESSA COR!");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\"#");
        
        //primeiro par de digito
        int offset = color / (symbols.length* symbols.length);
        sb.append(symbols[offset]);
        color -= (offset*symbols.length* symbols.length);
        
        //segundo par de digito
        offset = color / (symbols.length);
        sb.append(symbols[offset]);
        color -= (offset*symbols.length);
        
        //terceiro par de digito
        offset = color;
        sb.append(symbols[offset]);
        
        sb.append("\"");
        return sb;
    }
}
