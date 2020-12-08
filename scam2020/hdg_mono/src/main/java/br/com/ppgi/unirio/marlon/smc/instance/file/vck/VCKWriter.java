package br.com.ppgi.unirio.marlon.smc.instance.file.vck;

import br.com.ppgi.unirio.marlon.smc.instance.file.bunch.*;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
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
public class VCKWriter{
	private static final String SEPARATOR = "\t";
	
	/*
         * Primeira linha contem a quantidade de modulos
         * Segunda linha contém a quantidade de dependencias
         * Demais linhas contém as dependencias entre os módulos (numero do modulo (espaco) numero do outro modulo (espaco) peso da dependencia
         */
	
	public void execute(ModuleDependencyGraph mdg,String fileName) throws InstanceWriteException{
		try{
			File file = new File(fileName);
                        String fileName2 = fileName+"_";
                        File file2 = new File(fileName2);
                        
			if(file.exists()){
//				throw new InstanceWriteException("OUTPUT FILE ALREADY EXISTS, OPERATION WAS CANCELED!");
                                file.delete();
                                file2.delete();
			}
			file.createNewFile();
                        file2.createNewFile();

			PrintWriter  pw = new PrintWriter(fileName);			
			PrintWriter  pw2 = new PrintWriter(fileName2);
                        
                        //adiciona a quantidade de módulos
                        pw.println(mdg.getSize());
                        
                        //adiciona a quantidade de dependencias
                        pw.println(mdg.getTotalDependencyEdgeCount());
                        
			//adiciona as dependencias de cada modulo no arquivo de saida
			for(int originModule =0; originModule < mdg.getSize();originModule++){
                            pw2.println(originModule+SEPARATOR+mdg.getModuleNames().get(originModule));
                            for(int destModule=0; destModule<=originModule;destModule++){
                            
                                if(mdg.checkHasDependency(originModule, destModule)){
                                    pw.println(originModule+SEPARATOR+destModule+SEPARATOR+mdg.dependencyWeight(originModule, destModule));
                                }
                            }
			}
			pw.close();
                        pw2.close();
		}catch(IOException ioe){
			throw new InstanceWriteException(ioe);
		}
	}
}
