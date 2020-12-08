package br.com.ppgi.unirio.marlon.smc.instance.file.bunch;

import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import sobol.problems.clustering.generic.model.Dependency;
import sobol.problems.clustering.generic.model.Project;
import sobol.problems.clustering.generic.model.ProjectClass;

/**
 * Gera o conteudo de um arquivo Bunch a partir de um Project
 * @author kiko
 */
public class BunchWriter{
	private static final String SEPARATOR = "  ";
	
	/*
         * Cada linha possui uma classe e sua dependencia
         * terminar com uma linha em branco
         */
	
	public void execute(Project project,String fileName) throws InstanceWriteException{
		try{
			File file = new File(fileName);
			if(file.exists()){
				throw new InstanceWriteException("OUTPUT FILE ALREADY EXISTS, OPERATION WAS CANCELED!");
			}
			file.createNewFile();

			PrintWriter  pw = new PrintWriter(fileName);			
			
			//adiciona as dependencias da classe no arquivo de saida
			for(int i=0; i< project.getClassCount();i++){
				ProjectClass pc = project.getClassIndex(i);
				
				StringBuilder lineToWrite = new StringBuilder();
				
				for(Dependency dependency : pc.getDependencies()){
                                    pw.println(pc.getName()+SEPARATOR+dependency.getElementName());
				}
			}
			pw.close();
		}catch(IOException ioe){
			throw new InstanceWriteException(ioe);
		}
	}
}
