package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.alns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;

public class ALNSConfigurationBuilder{
    private static final boolean[] USE_SA = new boolean[]{false};
    private static final int[] ITERATION_LIMIT = new int[]{1000, 2000, 3000};
    private static final int[] TIME_LIMIT = new int[]{-1};
    
    private static final int[] NO_IMPROVEMENT_LIMIT = new int[]{-1};
    
    private static final float[] DESTRUCTION_FACTOR = new float[]{.1f, .2f, .3f};
    
    private static final float[] COOOLING_RATE = new float[]{0.9f, 0.99975f, 0.9999f};
    private static final float[] INITIAL_TEMPERATURE_RATIO = new float[]{0.02f, 0.05f, 0.1f};
    
    private static final float[] HC_THRESHOLD = new float[]{0.4f, 0.8f, 1f};
    
    private static final int[] REFRESH_SCORE_INTERVAL = new int[]{1,100,200};
    private static final float[] WEIGHT_BSF = new float[]{4f}; 
    private static final float[] WEIGHT_BTC = new float[]{2f};
    private static final float[] WEIGHT_WTCA = new float[]{1f, 0f};
    private static final float[] WEIGHT_WTCNA = new float[]{0f};
    private static final float[] REACTION_FACTOR = new float[]{0.1f, 0.5f, 1f};
    
    private static final int TOTAL_CONFIGURATIONS_AVAILABLE = calculateTotalConfigurationsAvailable();
    
    public static ALNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName){
        if(number >= TOTAL_CONFIGURATIONS_AVAILABLE){
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
        
        offset = tmp % DESTRUCTION_FACTOR.length;
        tmp /= DESTRUCTION_FACTOR.length;
        float destructionFactor = DESTRUCTION_FACTOR[offset];
         
        offset = tmp % COOOLING_RATE.length;
        tmp /= COOOLING_RATE.length;
        float coolingRate = COOOLING_RATE[offset];
         
        offset = tmp % INITIAL_TEMPERATURE_RATIO.length;
        tmp /= INITIAL_TEMPERATURE_RATIO.length;
         float initialTemperatureRatio = INITIAL_TEMPERATURE_RATIO[offset];
         
        offset = tmp % HC_THRESHOLD.length;
        tmp /= HC_THRESHOLD.length;
         float hcThreshold = HC_THRESHOLD[offset];
         
        offset = tmp % REFRESH_SCORE_INTERVAL.length;
        tmp /= REFRESH_SCORE_INTERVAL.length;
        int refreshScoreinterval = REFRESH_SCORE_INTERVAL[offset];
         
        offset = tmp % WEIGHT_BSF.length;
        tmp /= WEIGHT_BSF.length;
        float weightBSF = WEIGHT_BSF[offset];
         
        offset = tmp % WEIGHT_BTC.length;
        tmp /= WEIGHT_BTC.length;
        float weightBTC = WEIGHT_BTC[offset];
         
        offset = tmp % WEIGHT_WTCA.length;
        tmp /= WEIGHT_WTCA.length;
        float weightWTCA = WEIGHT_WTCA[offset];
         
        offset = tmp % WEIGHT_WTCNA.length;
        tmp /= WEIGHT_WTCNA.length;
        float weightWTCNA = WEIGHT_WTCNA[offset];
            
        offset = tmp % REACTION_FACTOR.length;
        tmp /= REACTION_FACTOR.length;
        float reactionFactor = REACTION_FACTOR[offset];
        
        ALNSConfiguration config = new ALNSConfiguration(mdg, outputTo, outPath, fileName);
        config.configure(useSA, iterationLimit, timeLimit, noImprovementLimit
                , destructionFactor, coolingRate
                , initialTemperatureRatio, hcThreshold
                , refreshScoreinterval, weightBSF
                , weightBTC, weightWTCA
                , weightWTCNA, reactionFactor
        );
        return config;
    }
    
    public static int calculateTotalConfigurationsAvailable(){
       /* return
            ITERATION_LIMIT.length * TIME_LIMIT.length * DESTRUCTION_FACTOR.length
            *COOOLING_RATE.length*INITIAL_TEMPERATURE_RATIO.length*HC_THRESHOLD.length
            *REFRESH_SCORE_INTERVAL.length*WEIGHT_BSF.length*WEIGHT_BTC.length
            *WEIGHT_WTCA.length*WEIGHT_WTCNA.length*REACTION_FACTOR.length
        ;*/
        return 2;
    }
    
    public static void main(String... args){
        int size = TOTAL_CONFIGURATIONS_AVAILABLE;
        System.out.println(size);
        
        for(int i=0; i< size; i++){
            ALNSConfiguration config = buildConfiguration(i, null, ResultWriter.OUTPUT.SYSO,"FAKE", null);
            System.out.println(i+": "+config.toString());
        }
    }
}

