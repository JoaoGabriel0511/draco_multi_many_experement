package br.com.ppgi.unirio.marlon.smc.instance.file.bunch;

import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Efetua a leitura de uma instância bunch para o objeto ModuleDependencyGraph
 * @author kiko
 */
public class BunchReader{
	
        private static final String SPLITTER = "\t";//split por qualquer espacamento
	/*
         * Cada linha possui uma classe e sua dependencia
         * terminar com uma linha em branco
         */
        Set<String> dependencies = new HashSet<>();
	
        /**
         * Efetua a leitura de uma instancia
         * @param path
         * @return
         * @throws InstanceParseException 
         */
	public ModuleDependencyGraph execute(String path) throws InstanceParseException{
		
		try{
                    //ler o arquivo até o final para descobrir quantas classes diferentes tem o arquivo
                    List<String> fileLines = readAllLines(path);	
                    List<String> modules = separateModules(fileLines);
                    
                    ModuleDependencyGraph mdg = new ModuleDependencyGraph(modules);
                        
                    for(String line : fileLines){
                        if(line.length() == 0){
                            continue;
                        }
                        String[] token = line.split(SPLITTER);
                        if(token.length  == 2){
                            int weight = mdg.addModuleDependency(token[0], token[1], 1);
                            String depName=token[0]+token[1];
                            if(!mdg.isWeighted() && dependencies.contains(depName)){
                                mdg.setWeighted(true);
                            }else{
                                dependencies.add(depName);
                            }
                        }else{
                            try {
                                mdg.addModuleDependency(token[0], token[1], Integer.parseInt(token[2]));
                                mdg.setWeighted(true);
                            }catch(Throwable t) {
                                System.out.println(line);
                                t.printStackTrace();
                            }
                        }
                        
                    }
                    mdg.setName(path.substring(path.lastIndexOf(File.separator)+1));
                    return mdg;
		}catch(IOException e){
			throw new InstanceParseException(e);
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
            List<String> modules = new ArrayList<>();
            
            NEXT_LINE:
            for(String line : lines){
                if(line.length() == 0){
                    continue;
                }
                String[] token = line.split(SPLITTER);
                
                if(token.length < 2){
                    throw new InstanceParseException("LINHA SEM DEPENDÊNCIA ENCONTRADA.");
                }
                //verificar se valor existe na lista de modulos
                boolean hasT0 = false;
                boolean hasT1 = false;
                for(String module : modules){
                    if(hasT0 && hasT1){continue NEXT_LINE;}
                    if(!hasT0 && token[0].equals(module)){hasT0 = true;}
                    if(!hasT1 && token[1].equals(module)){hasT1 = true;}
                }
                
                if(!hasT0){
                    modules.add(token[0]);
                }
                if(!hasT1 && !token[0].equals(token[1])){
                    modules.add(token[1]);
                }
            }
            return modules;
        }
}
