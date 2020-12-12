package br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive;

import java.util.ArrayList;
import java.util.List;

import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;

public class ConstrutiveDivisive  extends AConstrutiveSolutionBuilder{

    public static final String NAME="C3";
        
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int[] createSolution(ModuleDependencyGraph mdg){
        int[] solution = new ConstrutiveBasicAllModuleInSameClusterSolution().createSolution(mdg);

        //dividir os clusters iterativamente
        int[] newSolution = divisiveClustering(mdg, solution);

        return newSolution;
    }
    
    @Override
    public int[][] createSolution(ModuleDependencyGraph mdg, int quantity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }	
	
	
	
	private static int[] divisiveClustering(ModuleDependencyGraph mdg, int[] solution){
		int n = mdg.getSize();
		ClusterMetrics cm = new ClusterMetrics(mdg, solution);
		
		int[] maxMQSolution = cm.cloneSolution();
		double maxMQValue = cm.calculateMQ();
		
		int k=1;
		while(n-k>1){
			//selecionar elementos para a divisão
			int divideCluster = -1;
			int divideModule = -1;
			int minExternalDependency = Integer.MAX_VALUE;
			
			CLUSTER:
			for(int i=0;i<cm.getTotalClusteres();i++){//pegar o cluster			
				List<Integer> cluster = makeClusterList(cm,i);//montar uma lista com todos os módulos do cluster
				
				for(int modulePosition=1;modulePosition<cluster.size();modulePosition++){
				
					//iterar os modulos do cluster para verificar a maior dependência
					int currentExternalDependency = 0;
					for(int moduleindex=0;moduleindex<modulePosition;moduleindex++){
						int modulei = cluster.get(moduleindex);
						
						//verificar se existe dependencia entre esse modulo e os demais modulos do cluster
						for(int x=modulePosition;x<cluster.size();x++){
							int modulej = cluster.get(x);
                                                        
                                                        //logica nova
                                                        for(int dependencyi=0;dependencyi<mdg.getSize();dependencyi++){
                                                            int dependencyWeight = mdg.dependencyWeight(modulei, x);
                                                            if(dependencyWeight > 0 && dependencyi == modulej){
                                                                currentExternalDependency++;
                                                                break;
                                                            }
                                                        }
                                                        /*
							for(int dependencyIndex = 0; dependencyIndex < mdg.getModuleDependencyCount(modulei);dependencyIndex++){
								int dependencyi = mdg.getModuleDependency(modulei)[dependencyIndex];
								if(dependencyi == modulej){
									currentExternalDependency++;
									break;
								}
							}*/
						}	
					}
					if(currentExternalDependency < minExternalDependency){
						divideCluster = i;
						divideModule = modulePosition;
						minExternalDependency = currentExternalDependency;
						if(minExternalDependency == 0){
							break CLUSTER;
						}
					}
				}
			}
			
			
			
			//algutinar elementos
			if(minExternalDependency < Integer.MAX_VALUE){
				divideClusters(cm,divideCluster,divideModule);

				//verificar se o MQ vai aumentar
				double solutionMQ = cm.calculateMQ();
				
				if(solutionMQ > maxMQValue){//manter a melhor solução em memória
					maxMQValue = solutionMQ;
					maxMQSolution = cm.cloneSolution();
				}
				
//				System.out.println("DIVIDIDO CLUSTER: "+divideCluster+" - POSICAO: "+divideModule+" - Dep: "+minExternalDependency+" - TOT: "+clusters.size());
//				System.out.println("CURRENT MQ: "+sm.calculateMQ());
			}else{
				return maxMQSolution;
			}
			
			k += 1;
		}
		return maxMQSolution;
	}
		
	
	private static void divideClusters(ClusterMetrics cm, int divideCluster, int divideModule){
		List<Integer> cluster = makeClusterList(cm,divideCluster);//montar uma lista com todos os módulos do cluster
		
		int newClusterNumber = cm.getTotalClusteres();
		
		//mover todos os modulos que estão além do ponto de quebra para o outro cluster
		for(int index=divideModule; index <cluster.size();){
			cm.makeMoviment(cluster.get(index), newClusterNumber);
			cluster.remove(0);
		}
	}
		
	
	private static List<Integer> makeClusterList(ClusterMetrics cm, int cluster){
		//montar uma lista com todos os módulos do cluster
		List<Integer> clusterList = new ArrayList<>();
		
		//adicionar todos os modulos que estão nesse cluster a lista
		for(int module=0;module < cm.getSolution().length;module++){
			if(cm.getSolution()[module] == cluster){//modulo está no cluster atual
				clusterList.add(module);
			}
		}
		return clusterList;
	}

    
		
		
		
}
