package br.com.ppgi.unirio.marlon.smc.instance.file.mdg;

import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceFileWorker;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.File;
import sobol.problems.clustering.generic.model.Project;

public class MDGInstanceFileWorker extends InstanceFileWorker<ModuleDependencyGraph>{
	
	@Override
	protected String getInstanceFolder(){
		return INSTANCES_BASE_FOLDER+"mdg";
	}
	
	@Override
	public ModuleDependencyGraph readInstanceFile(File currentInstance) throws InstanceParseException {
		MDGReader mdgReader = new MDGReader();
		ModuleDependencyGraph mdg = mdgReader.execute(currentInstance.getAbsolutePath());
		return mdg;
	}

	@Override
	public void writeInstanceFile(Project project) throws InstanceWriteException {
		File directory = new File(getInstanceFolder());
		if(!directory.isDirectory()){
			directory.mkdir();
		}
		String fileName = getInstanceFolder()+"/"+project.getName()+" "+project.getClassCount()+"C.mdg";
		MDGWriter mdgWriter = new MDGWriter();
		mdgWriter.execute(project, fileName);
	}

    @Override
    public void writeInstanceFile(ModuleDependencyGraph mdg) throws InstanceWriteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
