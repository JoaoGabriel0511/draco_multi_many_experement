package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory;
import java.util.Map;
import random.number.generator.RandomWrapper;

public class LNSConfigurationBuilderRandom extends LNSConfigurationBase{
    public static enum FILTER_NAMES {
            USE_SIMMULATED_ANNEALING
            , ITERATION_LIMIT
            , TIME_LIMIT
            , NO_IMPROVEMENT_LIMIT
            , INITIAL_SOLUTION_METHOD
            , DESTRUTIVE_METHOD
            , REPAIR_METHOD
            , DESTRUCTION_FACTOR
            , COOOLING_RATE
            , INITIAL_TEMPERATURE_RATIO
            , NONE
            
            , DESTRUCTION_FACTOR_PROBABILITY
            , ALGORITHM_NO_IMPROVEMENT_LIMIT
            , MIXED_RESTART
            , ALGORITHM_NO_IMPROVEMENT_LIMIT_REDUCTION_FACTOR
    };
    
   public LNSConfiguration buildRandomConfiguration (ModuleDependencyGraph mdg, Map<FILTER_NAMES, Object[]> fixedValues){
        boolean useSa = createUseSA(fixedValues.get(FILTER_NAMES.USE_SIMMULATED_ANNEALING));
        int iterationLimit = createIterationLimit(fixedValues.get(FILTER_NAMES.ITERATION_LIMIT));
        int timeLimit = createTimeLimit(fixedValues.get(FILTER_NAMES.TIME_LIMIT));
        int noImprovementLimit = createNoImprovementLimit(fixedValues.get(FILTER_NAMES.NO_IMPROVEMENT_LIMIT));
        int algorithNoImprovementLimit = createNoImprovementLimit(fixedValues.get(FILTER_NAMES.ALGORITHM_NO_IMPROVEMENT_LIMIT));
        InitialSolutionFactory.CREATION_METHOD ism = createInitialSolutionMethod(fixedValues.get(FILTER_NAMES.INITIAL_SOLUTION_METHOD));
        DestrutiveMethodFactory.DESTRUTIVE_METHOD dm = createDestrutiveMethod(fixedValues.get(FILTER_NAMES.DESTRUTIVE_METHOD));
        
        float destructionFactor = createDestructionFactor(fixedValues.get(FILTER_NAMES.DESTRUCTION_FACTOR));
        
        float coolingRate = createCoolingRate(useSa, fixedValues.get(FILTER_NAMES.COOOLING_RATE));
        float initialTemperatureRatio = createInitialTemperatureRatio(useSa, fixedValues.get(FILTER_NAMES.INITIAL_TEMPERATURE_RATIO));
       
        LNSConfiguration config = new LNSConfiguration(mdg, null, null, null);
        
        if(algorithNoImprovementLimit == -1){
            RepairMethodFactory.REPAIR_METHOD rm = createRepairMethod(fixedValues.get(FILTER_NAMES.REPAIR_METHOD));
            config.configure(useSa, iterationLimit, timeLimit, noImprovementLimit
                    , destructionFactor, coolingRate
                    , initialTemperatureRatio
                    ,ism, dm, rm
            );
        }else{//busca com mudança de algoritmo
            Object[] dfsO = fixedValues.get(FILTER_NAMES.DESTRUCTION_FACTOR);
            float[] dfs = new float[dfsO.length];
            for (int i=0;i< dfsO.length;i++) {
                dfs[i] = (float) dfsO[i];
            }
            
            Object[] dfspO = fixedValues.get(FILTER_NAMES.DESTRUCTION_FACTOR_PROBABILITY);
            int[] dfps = new int[dfspO.length];
            for (int i=0;i< dfspO.length;i++) {
                dfps[i] = (int) dfspO[i];
            }
            
            Object[] rmsO = fixedValues.get(FILTER_NAMES.REPAIR_METHOD);//todos os metodos de reparo serão utilizados
            RepairMethodFactory.REPAIR_METHOD rms[] = new RepairMethodFactory.REPAIR_METHOD[rmsO.length];
            for (int i=0;i< rmsO.length;i++) {
                rms[i] = (RepairMethodFactory.REPAIR_METHOD) rmsO[i];
            }
            
            Object[] mixedRestartO = fixedValues.get(FILTER_NAMES.MIXED_RESTART);//todos os metodos de reparo serão utilizados
            boolean mixedRestart = mixedRestartO != null ? (boolean)mixedRestartO[0] : true;
            
            Object[] reductionFactorO = fixedValues.get(FILTER_NAMES.ALGORITHM_NO_IMPROVEMENT_LIMIT_REDUCTION_FACTOR);
            double reductionFactor = reductionFactorO != null ? (double)reductionFactorO[0] : 1;
            
            config.configure(useSa, iterationLimit, timeLimit, noImprovementLimit, algorithNoImprovementLimit
                    , dfs, dfps, reductionFactor, mixedRestart
                    , coolingRate, initialTemperatureRatio
                    ,ism, dm, rms
            );
        }
        return config;
   }
   
   public void changeParameterValue(LNSConfiguration config, FILTER_NAMES parameter, Object value){
       changeParameterValue(config, parameter, new Object[]{value});
   }
   
   private void changeParameterValue(LNSConfiguration config, FILTER_NAMES parameter, Object[] value){
       if(parameter != null){
            switch(parameter){
                case ITERATION_LIMIT: config.iterationLimit = createIterationLimit(value); break;
                case TIME_LIMIT: config.timeLimit = createTimeLimit(value); break;

                case INITIAL_SOLUTION_METHOD: config.initialMethod = createInitialSolutionMethod(value); break;
                case DESTRUTIVE_METHOD: config.destrutiveMethod = createDestrutiveMethod(value); break;
                case REPAIR_METHOD: config.repairMethod = createRepairMethod(value); break;

                case DESTRUCTION_FACTOR: config.destructionFactor = createDestructionFactor(value); break;
                case COOOLING_RATE: config.coolingRate = createCoolingRate(config.useSA, value); break;
                case INITIAL_TEMPERATURE_RATIO: config.inicialTemperatureRatio = createInitialTemperatureRatio(config.useSA,value); break;
                case NONE:break;
                default : throw new RuntimeException("PARAMETRO DESCONHECIDO");
            }
        }
       
       config.buildConfiguration();//atualiza os valores dos algoritmos
      
   }
   
   protected boolean createUseSA(Object[] value){//5
       if(value != null) return (boolean) selectItemFromArray(value);
       int minValue = 0;
       int maxValue = 1;
       int gapValue = 1;
       return RandomWrapper.createRandomNumber(minValue, maxValue, gapValue)==0;
   }
   
   protected int createIterationLimit(Object[] value){//5
       if(value != null) return (int) selectItemFromArray(value);
       int minValue = 1000;
       int maxValue = 5000;
       int gapValue = 1000;
       return RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
   }
   
   protected int createTimeLimit(Object[] value){//1
       if(value != null) return (int) selectItemFromArray(value);
       return -1;
   }
   
   protected int createNoImprovementLimit(Object[] value){//1
       if(value != null) return (int) selectItemFromArray(value);
       return -1;
   }
   
   
   protected InitialSolutionFactory.CREATION_METHOD createInitialSolutionMethod(Object[] value){//2
        if(value != null) return (InitialSolutionFactory.CREATION_METHOD) selectItemFromArray(value);
        int minValue = 0;
        int maxValue = InitialSolutionFactory.totalKnownAlgorithms()-1;
        int gapValue = 1;
        int rand = RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
        return InitialSolutionFactory.getMethod(rand);
   }
   
   protected DestrutiveMethodFactory.DESTRUTIVE_METHOD createDestrutiveMethod(Object[] value){//8
        if(value != null){
            return (DestrutiveMethodFactory.DESTRUTIVE_METHOD) selectItemFromArray(value);
        }
        int minValue = 0;
        int maxValue = DestrutiveMethodFactory.totalKnownAlgorithms()-1;
        int gapValue = 1;
        int rand = RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
        return DestrutiveMethodFactory.getMethod(rand);
   }
   
   protected RepairMethodFactory.REPAIR_METHOD createRepairMethod(Object[] value){//2
        if(value != null) return (RepairMethodFactory.REPAIR_METHOD) selectItemFromArray(value);
        int minValue = 0;
        int maxValue = RepairMethodFactory.totalKnownAlgorithms()-1;
        int gapValue = 1;
        int rand = RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
        return RepairMethodFactory.getMethod(rand);
   }
   
   protected float createDestructionFactor(Object[] value){//700
       if(value != null) return (float) selectItemFromArray(value);
       float minValue = 0.1f;
       float maxValue = 0.8f;
       float gapValue = 0.001f;
       return RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
   }
   
   protected float createCoolingRate(boolean useSA, Object[] value){//1999
       if(!useSA) return -1;
       if(value != null) return (float) selectItemFromArray(value);
       float minValue = 0.8f;
       float maxValue = 0.9999f;
       float gapValue = 0.0001f;
       return RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
   }
   
   protected float createInitialTemperatureRatio(boolean useSA, Object[] value){//89
       if(!useSA) return -1;
       if(value != null) return (float) selectItemFromArray(value);
       float minValue = 0.01f;
       float maxValue = 0.9f;
       float gapValue = 0.01f;
       return RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
   }
   
   protected Object selectItemFromArray(Object[] array){
        int minValue = 0;
        int maxValue = array.length-1;
        int gapValue = 1;
        
        if(array.length == 0){
            return null;
        }
        if(array.length == 1){
            return array[0];
        }
        int rand = RandomWrapper.createRandomNumber(minValue, maxValue, gapValue);
        return array[rand];
   }
   
   
    @Override
    public LNSConfiguration buildConfiguration (int number, ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName){
        throw new RuntimeException("ESCOLHA OS PARAMETROS FIXOS.");
    }

    @Override
    public int calculateTotalConfigurationsAvailable(){
       throw new RuntimeException("INFINITAS COMBINAÇÕES ALEATÓRIAS");
    }
}

