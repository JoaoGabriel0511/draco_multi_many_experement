package br.com.ppgi.unirio.marlon.smc.instance.file.odem;

import java.io.File;

import javax.management.modelmbean.XMLParseException;

import sobol.problems.clustering.generic.model.Project;
import sobol.problems.clustering.generic.reader.CDAReader;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceFileWorker;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

public class OdemInstanceFileWorker extends InstanceFileWorker<Project>{
	
	@Override
	protected String getInstanceFolder(){
		return INSTANCES_BASE_FOLDER+"odem";
	}
	
	@Override
	public Project readInstanceFile(File currentInstance) throws InstanceParseException {
		try{
			CDAReader cdaReader = new CDAReader();//leitor de instancias do marcio
			Project project = cdaReader.execute(currentInstance.getAbsolutePath());
			return project;
		}catch(XMLParseException xmlpe){
			throw new InstanceParseException(xmlpe);
		}
	}
	
	@Override
	public void writeInstanceFile(Project project) throws InstanceWriteException {
		throw new InstanceWriteException("THE CREATION OF ODEM INSTANCE FILES WAS NOT IMPLEMENTED");
	}
        
        @Override
    public void writeInstanceFile(ModuleDependencyGraph mdg) throws InstanceWriteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
