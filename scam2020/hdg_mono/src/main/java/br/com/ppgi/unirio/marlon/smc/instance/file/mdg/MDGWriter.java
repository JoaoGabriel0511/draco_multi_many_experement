package br.com.ppgi.unirio.marlon.smc.instance.file.mdg;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import sobol.problems.clustering.generic.model.Dependency;
import sobol.problems.clustering.generic.model.Project;
import sobol.problems.clustering.generic.model.ProjectClass;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;

public class MDGWriter{
	private static final String SEPARATOR = " ";
	
	/*
	 * N classes
	 * para cada a quantidade de dependencias desta, e para cada dependencia, o seu valor e o peso intercalados, por linha
	 * ...
	 * ...
	 * ... N linhas
	 * 
	 * nome projeto
	 * nome  de cada classe em uma linha 
	 * ...
	 * ...
	 * ... N linhas
	 * 
	 * 
	 */
	
	public void execute(Project project,String fileName) throws InstanceWriteException{
		try{
			File file = new File(fileName);
			if(file.exists()){
				throw new InstanceWriteException("OUTPUT FILE ALREADY EXISTS, OPERATION WAS CANCELED!");
			}
			file.createNewFile();

			PrintWriter  pw = new PrintWriter(fileName);			
			pw.println(project.getClassCount()+"");
			
			
			
			//adiciona as dependencias da classe no arquivo de saida
			for(int i=0; i< project.getClassCount();i++){
				ProjectClass pc = project.getClassIndex(i);
				
				StringBuilder lineToWrite = new StringBuilder();
				
				lineToWrite.append(pc.getDependencyCount()+SEPARATOR);//adiciona o total de dependencias - um por linha
				for(Dependency dependency : pc.getDependencies()){
					int dependencyIndex = project.getClassIndex(dependency.getElementName())+1;
					lineToWrite.append(dependencyIndex+SEPARATOR);//adiciona o valor da dependencia
					
					int weight = 1;//instancias nao tem peso, todas serão consideradas 1
					lineToWrite.append(weight+SEPARATOR);//adiciona o peso da dependencia
				}
				if(lineToWrite.length() > 0){
					lineToWrite.deleteCharAt(lineToWrite.length()-1);//remove ultimo espaco
				}
			
				pw.println(lineToWrite);
			}
			
			//nome do projeto
			pw.println(project.getName());
			
			//adiciona os nomes das classes no arquivo de saida
			for(int i=0; i< project.getClassCount();i++){
				ProjectClass pc = project.getClassIndex(i);
				pw.println(pc.getName());
			}
			pw.close();
		}catch(IOException ioe){
			throw new InstanceWriteException(ioe);
		}
	}
}
