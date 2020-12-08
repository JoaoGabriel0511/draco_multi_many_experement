package br.com.ppgi.unirio.marlon.smc.instance.file.bunch;

import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceFileWorker;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceParseException;
import br.com.ppgi.unirio.marlon.smc.instance.file.InstanceWriteException;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import java.io.File;
import sobol.problems.clustering.generic.model.Project;

public class BunchInstanceFileWorker extends InstanceFileWorker<ModuleDependencyGraph>{
	
    private String folder = "bunch";

    public BunchInstanceFileWorker(String folder){
        super();
        this.folder = folder;
    }
    
    public BunchInstanceFileWorker(){
        super();
    }
    
    @Override
    protected String getInstanceFolder(){
        return INSTANCES_BASE_FOLDER+folder;
    }

    @Override
    public ModuleDependencyGraph readInstanceFile(File currentInstance) throws InstanceParseException {
        BunchReader reader = new BunchReader();
        ModuleDependencyGraph mdg = reader.execute(currentInstance.getAbsolutePath());
        return mdg;
    }

    @Override
    public void writeInstanceFile(Project project) throws InstanceWriteException {
        File directory = new File(getInstanceFolder());
        if(!directory.isDirectory()){
                directory.mkdir();
        }
        String fileName = getInstanceFolder()+"/"+project.getName();
        BunchWriter writer = new BunchWriter();
        writer.execute(project, fileName);
    }
    
    @Override
    public void writeInstanceFile(ModuleDependencyGraph mdg) throws InstanceWriteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
