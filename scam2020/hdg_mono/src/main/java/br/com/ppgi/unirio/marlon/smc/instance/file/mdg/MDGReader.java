package br.com.ppgi.unirio.marlon.smc.instance.file.mdg;

import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MDGReader{
	
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
	
	public ModuleDependencyGraph execute(String path) throws InstanceParseException{
		
		try{
                    List<String> fileLines = readAllLines(path);	
                    List<String> modules = separateModules(fileLines);	
                    ModuleDependencyGraph mdg = new ModuleDependencyGraph(modules);
                    
                    	for(int i=0;i< modules.size(); i++){
				
				String line = fileLines.get(i+1);
				String[] moduleInfo = line.split(" ");
				int dependencySize = Integer.parseInt(moduleInfo[0]);
				
				for(int j=0;j<dependencySize;j++){
					int dependsOn = Integer.parseInt(moduleInfo[2*j+1])-1;
					int weight = Integer.parseInt(moduleInfo[2*j+2]);
					
					mdg.addModuleDependency(i, dependsOn, weight);
				}	
			}
			
			mdg.setName(fileLines.get( ((fileLines.size()-2)/2)+1) );
			
			return mdg;
		}catch(IOException e){
			throw new InstanceParseException(e);
		}finally{
			
		}
	}
        
        
        /**
         * Efetua a leitura de todas as linhas do arquivo e coloca cada linha como um elemento da lista
         * @param path
         * @return
         * @throws IOException
         * @throws InstanceParseException 
         */
        private List<String> readAllLines(String path) throws IOException, InstanceParseException{
            return Files.readAllLines(FileSystems.getDefault().getPath(path),StandardCharsets.UTF_8);
        }
        
        /**
         * Separa as linhas do arquivo em módulos unicos
         * @param lines
         * @return
         * @throws IOException
         * @throws InstanceParseException 
         */
        private List<String> separateModules(List<String> lines) throws IOException, InstanceParseException{
            //primeira linha é o total
            //N linhas de relacionamentos
            //nome da instancia
            //N linhas de nome
            //linha em branco
            int offset = ((lines.size()-2)/2)+2;
            List<String> modules = new ArrayList<>();
            
            for(int i=offset;i< lines.size();i++){
                modules.add(lines.get(i));
            }
            return modules;
        }
}
