package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory;

public class LNSConfigurationBuilderMultiply extends LNSConfigurationBase{
    private static final boolean[] USE_SA = new boolean[]{false};
    private static final int[] ITERATION_LIMIT = new int[]{3000};
    private static final int[] TIME_LIMIT = new int[]{-1};
    private static final int[] NO_IMPROVEMENT_LIMIT = new int[]{-1};
    
    private static final int TOTAL_INITIAL_SOLUTION = InitialSolutionFactory.totalKnownAlgorithms();
    private static final int TOTAL_DESTRUTIVE_METHOD = DestrutiveMethodFactory.totalKnownAlgorithms();
    private static final int TOTAL_REPAIR_METHOD = 2;/*RepairMethodFactory.totalKnownAlgorithms();*/
    
    private static final float[] DESTRUCTION_FACTOR = new float[]{.2f, .4f, .6f};
    private static final float[] COOOLING_RATE = new float[]{0.99951f, 0.99975f, 0.99999f};
    private static final float[] INITIAL_TEMPERATURE_RATIO = new float[]{0.1f, 0.05f, 0.09f};
    
    
   
    @Override
    public LNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName){
        if(number >= calculateTotalConfigurationsAvailable()){
            return null;//não possui mais 
        }
        int tmp=number;
        
        int offset = tmp % USE_SA.length;
        tmp /= USE_SA.length;
        boolean useSA = USE_SA[offset];
        
        offset = tmp % ITERATION_LIMIT.length;
        tmp /= ITERATION_LIMIT.length;
        int iterationLimit = ITERATION_LIMIT[offset];
        
        
        offset = tmp % TIME_LIMIT.length;
        tmp /= TIME_LIMIT.length;
        int timeLimit = TIME_LIMIT[offset];
        
        offset = tmp % NO_IMPROVEMENT_LIMIT.length;
        tmp /= NO_IMPROVEMENT_LIMIT.length;
        int noImprovementLimit = NO_IMPROVEMENT_LIMIT[offset];
        
        
        offset = tmp % TOTAL_INITIAL_SOLUTION;
        tmp /= TOTAL_INITIAL_SOLUTION;
        InitialSolutionFactory.CREATION_METHOD initialSolutionMethod = InitialSolutionFactory.getMethod(offset);
        
        offset = tmp % TOTAL_DESTRUTIVE_METHOD;
        tmp /= TOTAL_DESTRUTIVE_METHOD;
        DestrutiveMethodFactory.DESTRUTIVE_METHOD destrutiveMethod = DestrutiveMethodFactory.getMethod(offset);
        
        offset = tmp % TOTAL_REPAIR_METHOD;
        tmp /= TOTAL_REPAIR_METHOD;
        RepairMethodFactory.REPAIR_METHOD repairMethod = RepairMethodFactory.getMethod(offset);
        
        offset = tmp % DESTRUCTION_FACTOR.length;
        tmp /= DESTRUCTION_FACTOR.length;
        float destructionFactor = DESTRUCTION_FACTOR[offset];
         
        offset = tmp % COOOLING_RATE.length;
        tmp /= COOOLING_RATE.length;
        float coolingRate = COOOLING_RATE[offset];
         
        offset = tmp % INITIAL_TEMPERATURE_RATIO.length;
        tmp /= INITIAL_TEMPERATURE_RATIO.length;
         float initialTemperatureRatio = INITIAL_TEMPERATURE_RATIO[offset];
         
        
        LNSConfiguration config = new LNSConfiguration(mdg, outputTo, outPath, fileName);
        config.configure(useSA, iterationLimit, timeLimit, noImprovementLimit
                , destructionFactor, coolingRate
                , initialTemperatureRatio
                ,initialSolutionMethod, destrutiveMethod, repairMethod
        );
        return config;
    }
    
    @Override
    public int calculateTotalConfigurationsAvailable(){
       return
            ITERATION_LIMIT.length * TIME_LIMIT.length 
               * TOTAL_INITIAL_SOLUTION * TOTAL_DESTRUTIVE_METHOD * TOTAL_REPAIR_METHOD
               * DESTRUCTION_FACTOR.length
               * COOOLING_RATE.length*INITIAL_TEMPERATURE_RATIO.length
        ;
    }
}

