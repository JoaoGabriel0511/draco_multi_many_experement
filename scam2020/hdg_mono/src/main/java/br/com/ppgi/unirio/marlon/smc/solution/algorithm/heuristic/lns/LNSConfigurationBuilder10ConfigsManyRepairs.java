package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import static br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory.CREATION_METHOD;
import static br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory.DESTRUTIVE_METHOD;
import static br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory.REPAIR_METHOD;

public class LNSConfigurationBuilder10ConfigsManyRepairs extends LNSConfigurationBase{
    private static final boolean[] USE_SA = new boolean[]{false,false,false,false,false,false,false,false,false,false};//10 configs
    private static final int[] ITERATION_LIMIT = new int[]{2000,2000,2000,2000,2000,2000,2000,2000,2000,2000};//10 configs
    private static final int[] TIME_LIMIT = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    private static final int[] NO_ITERATION_LIMIT = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    
    private static final float[] DESTRUCTION_FACTOR = new float[]{.4f,.4f,.4f,.4f,.4f,.4f,.4f,.2f,.4f,.4f};
    private static final float[] COOOLING_RATE = new float[]{0.99975f,0.99951f,0.99975f,0.99999f,0.99951f,0.99975f,0.99999f,0.99999f,0.99951f,0.99999f};
    private static final float[] INITIAL_TEMPERATURE_RATIO = new float[]{0.09f,0.09f,0.05f,0.09f,0.1f,0.1f,0.1f,0.09f,0.05f,0.05f};
    
    private static final CREATION_METHOD[] INITIAL_SOLUTION_METHODS = new CREATION_METHOD[] {CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ,CREATION_METHOD.CAMQ};
    private static final DESTRUTIVE_METHOD[] DESTRUTIVE_METHODS = new DESTRUTIVE_METHOD [] {DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR,DESTRUTIVE_METHOD.DR};
    
    
    private static final REPAIR_METHOD[] REPAIR_METHODS = new REPAIR_METHOD[] {REPAIR_METHOD.RGBIR, /*REPAIR_METHOD.RGWIR/*, REPAIR_METHOD.RGIWMF*/};
    
    
    @Override
    public LNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName){
        if(number >= calculateTotalConfigurationsAvailable()){
            return null;//não possui mais 
        }
         
        int offset = number/calculateTotalConfigurationsAvailable();
        int repairN = number % calculateTotalConfigurationsAvailable();
        
        LNSConfiguration config = new LNSConfiguration(mdg, outputTo, outPath, fileName);
        config.configure(USE_SA[offset], ITERATION_LIMIT[offset], TIME_LIMIT[offset], NO_ITERATION_LIMIT[offset]
                , DESTRUCTION_FACTOR[offset], COOOLING_RATE[offset]
                , INITIAL_TEMPERATURE_RATIO[offset]
                , INITIAL_SOLUTION_METHODS[offset], DESTRUTIVE_METHODS[offset], REPAIR_METHODS[repairN]
        );
        return config;
    }
    
    @Override
    public int calculateTotalConfigurationsAvailable(){
       return
               /*ITERATION_LIMIT.length * */REPAIR_METHODS.length;
    }
}

