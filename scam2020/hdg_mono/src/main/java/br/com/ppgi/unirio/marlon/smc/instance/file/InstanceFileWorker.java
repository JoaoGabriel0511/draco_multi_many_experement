package br.com.ppgi.unirio.marlon.smc.instance.file;


import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.File;
import sobol.problems.clustering.generic.model.Project;

public abstract class InstanceFileWorker<T extends Object> {
    
        public static final String INSTANCES_BASE_FOLDER="../instances/";
	
	/**
         * Retorna todos os arquivos de um ditretorio
         * @return 
         */
	public File[] retrieveAllInstanceFiles(){
		File directory = new File(getInstanceFolder());
		File[] instanceFiles = directory == null || !directory.isDirectory() ? null : directory.listFiles();
		assert instanceFiles != null && instanceFiles.length > 0 : "NO INSTANCE WAS FOUND!";
		return instanceFiles;
	}
	
	/**
         * Efetua a leitura de um arquivo de inst�ncia
         * @param currentInstance
         * @return
         * @throws InstanceParseException 
         */
        public abstract T readInstanceFile(File currentInstance) throws InstanceParseException;
	
        /**
         * Gera um arquivo de ins�ncia a partir de um projeto
         * @param project
         * @throws InstanceWriteException 
         */
        public abstract void writeInstanceFile(Project project) throws InstanceWriteException;
        
        /**
         * Gera um arquivo de ins�ncia a partir de um grafo MDG
         * @param mdg
         * @throws InstanceWriteException 
         */
        public abstract void writeInstanceFile(ModuleDependencyGraph mdg) throws InstanceWriteException;
	
	/**
         * Retorna a pasta onde as ins�ncias est�o
         * @return 
         */
        protected abstract String getInstanceFolder();
}
