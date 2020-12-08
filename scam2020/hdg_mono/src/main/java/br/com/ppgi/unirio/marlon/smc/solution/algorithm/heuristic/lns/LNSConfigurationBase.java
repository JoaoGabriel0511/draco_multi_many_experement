package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

public abstract class LNSConfigurationBase{
    
    public abstract LNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName);
    public LNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg){
        return buildConfiguration(number, mdg, null, null, null);
    }
    public abstract int calculateTotalConfigurationsAvailable();
}
