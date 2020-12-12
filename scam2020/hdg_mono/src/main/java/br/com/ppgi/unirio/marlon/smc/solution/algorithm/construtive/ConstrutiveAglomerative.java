package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

public class ConstrutiveAglomerative  extends AConstrutiveSolutionBuilder{
    
    public static final String NAME="C1";
    
    @Override
    public String getName(){
        return NAME;
    }
    
    @Override
    public int[] createSolution(ModuleDependencyGraph mdg){
        int[] solution = new ConstrutiveBasicOneModulePerCluster().createSolution(mdg);
		
        //aglomerar os clusteres iterativamente
        int[] newSolution = aglomerateClustering(mdg, solution);

        return newSolution;
    }
    
    @Override
    public int[][] createSolution(ModuleDependencyGraph mdg, int quantity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
	
    private static int[] aglomerateClustering(ModuleDependencyGraph mdg, int[] solution){
        int n = mdg.getSize();
        ClusterMetrics cm = new ClusterMetrics(mdg, solution);

        int[] maxMQSolution = cm.cloneSolution();
        double maxMQValue = cm.calculateMQ();
        int[][] algutinateDependency = new int[cm.getTotalClusteres()][cm.getTotalClusteres()];
        int k=1;
        while(n-k>1){
                //selecionar elementos para a aglutinação
                //int[] currentSolution = cm.cloneSolution();
                //int[][] algutinateDependency = new int[cm.getTotalClusteres()][cm.getTotalClusteres()];
                int aglutinatei = -1;
                int aglutinatej = -1;
                int maxInternalDependency=-1;
                for(int i=0;i<cm.getTotalClusteres();i++){for(int j=0;j<cm.getTotalClusteres();j++){algutinateDependency[i][j] = 0;}}//inicializa os contadores de dependencia

                //itera todos os módulos para descobrir a uniao dos clusteres com a maior dependencia interna
                for(int modulei=0; modulei< mdg.getSize();modulei++){
                        for(int modulej=modulei+1; modulej< mdg.getSize();modulej++){
                                int clusteri = cm.convertToClusterNumber(modulei);
                                int clusterj = cm.convertToClusterNumber(modulej);
                                algutinateDependency[clusteri][clusterj] += mdg.dependencyWeight(modulei, modulej);
                                algutinateDependency[clusteri][clusterj] += mdg.dependencyWeight(modulej, modulei);

                                if(algutinateDependency[clusteri][clusterj] > maxInternalDependency){
                                        maxInternalDependency = algutinateDependency[clusteri][clusterj];
                                        aglutinatei = clusteri;
                                        aglutinatej = clusterj;
                                }
                        }
                }



                if(maxInternalDependency > 0){//algutinar elementos
                        cm.makeMergeClusters(aglutinatei,aglutinatej);//efetua a aglutinação dos clusteres

                        //verificar se o MQ vai aumentar
                        double solutionMQ = cm.calculateMQ();

                        if(solutionMQ > maxMQValue){//manter a melhor solução em memória
                                maxMQValue = solutionMQ;
                                maxMQSolution = cm.cloneSolution();
                        }

    //				System.out.println("AGLUTINANDO: "+aglutinatei+" - "+aglutinatej+" - Dep: "+maxInternalDependency+" - TOT: "+cm.getTotalClusteres());
    //				System.out.println("CURRENT MQ: "+cm.calculateMQ());
                }else{
                        return maxMQSolution;
                }
                k += 1;
        }
        return maxMQSolution;
    }
}
