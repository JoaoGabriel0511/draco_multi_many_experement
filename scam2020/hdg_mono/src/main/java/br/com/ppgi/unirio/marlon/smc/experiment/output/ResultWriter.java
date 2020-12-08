package br.com.ppgi.unirio.marlon.smc.experiment.output;

import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Represatação abstrata da saida de dados do experimento
 * @author kiko
 */
public abstract class ResultWriter {
	
	public static final String C_S= ";";
	public static enum OUTPUT {SYSO, FILE};
	
	protected PrintStream out;
		
	
	public static ResultWriter configureResultWriter(OUTPUT outputTo) throws IOException{
		return configureResultWriter(outputTo, "", null);
	}
	
	public static ResultWriter configureResultWriter(OUTPUT outputTo,String folderName,String filePrefix) throws IOException{
            return configureResultWriter(outputTo, folderName, filePrefix, "dat");
        }
        
        public static ResultWriter configureResultWriter(OUTPUT outputTo,String folderName, String filePrefix, String ext) throws IOException{
		if(outputTo == OUTPUT.FILE){
			return new FileResultWriter(folderName, filePrefix, ext);
		}else{
			return new SystemOutResultWriter();
		}
	}
	
	/*
	public void writeHeader(){
		out.println("INSTÂNCIA"+C_S+"ALGORITMO"+C_S+"ITERAÇÃO"+C_S+"MQ"+C_S+"TEMPO"+C_S+"SOLUÇÃO");
	}
	
	public void writeInstancePropertiesHeader(){
		out.println("INSTÂNCIA"+C_S+"MÓDULOS"+C_S+"DEPENDÊNCIAS");
	}
	
	public void writeInstanceProperties(ModuleDependencyGraph mdg){
		out.println(mdg.getName()+C_S+mdg.getSize()+C_S+mdg.getTotalDependencyCount());
	}*/
	/*
	public void writeLine(ModuleDependencyGraph mdg, int[] solution,double mq, String algorithmName, long iterationN, long time){
		out.print(mdg.getName()+C_S+algorithmName+C_S+iterationN+C_S+(""+mq).toString().replace(".", ",")+C_S+time);
		for(int i=0;i<solution.length;i++){
			out.print(solution[i]+" ");
		}
		out.println();
	}*/
	
	public void writeLine(ClusterMetrics cm,double maxMQ, long bestSolutionIteration, long iterationN, long time, String algorithmName, String params){
		out.println(cm.getMdg().getName()+C_S+cm.getMdg().getSize()+C_S+algorithmName+C_S+params+C_S+iterationN+C_S+cm.calculateMQ()+C_S+maxMQ+C_S+bestSolutionIteration+C_S+time+C_S+cm.getTotalClusteres()+C_S+cm.getBiggestClusterSize()+C_S+cm.getSmallestClusterSize()+C_S+cm.getIsolatedClusterCount()+C_S+cm.smallestClusterMF()+C_S+cm.biggestClusterMF());
	}
        
        public void writeGraph(StringBuilder graph){
            out.println(graph);
        }
        
        public void writeLine(String... values){
            if(values.length>0){
                out.print(values[0]);
            }
            for(int i=1;i<values.length;i++){
                out.print(C_S+values[i]);
            }
            out.println();
        }
	
	public void close(){
		out.close();
	}
}
