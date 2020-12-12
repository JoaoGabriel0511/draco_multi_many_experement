package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory;
import random.number.generator.RandomWrapper;

public class LNSConfigurationBuilderFixedRandom extends LNSConfigurationBuilderRandom{
    
    public static final Float[] DESTRUCTION_FACTOR_VALUES = {0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f};
    public static final Float[] COOLING_RATE_VALUES = {0.9998f, 0.99975f, 0.9995f, 0.999f, 0.998f, 0.99f, 0.9f};
    public static final Float[] INITIAL_TEMPERATURE_RATIO_VALUES = {0.9f, 0.5f, 0.2f, 0.1f, 0.05f, 0.025f, 0.01f};
   
   
   @Override
   protected int createIterationLimit(Object[] value){//1
       if(value != null) return (int) selectItemFromArray(value);
       return 2000;
   }
   
   @Override
   protected int createTimeLimit(Object[] value){//1
       if(value != null) return (int) selectItemFromArray(value);
       return -1;
   }
   
   @Override
   protected InitialSolutionFactory.CREATION_METHOD createInitialSolutionMethod(Object[] value){//1
        return super.createInitialSolutionMethod(value);
   }
   
   @Override
   protected DestrutiveMethodFactory.DESTRUTIVE_METHOD createDestrutiveMethod(Object[] value){//8
        return super.createDestrutiveMethod(value);
   }
   
   @Override
   protected RepairMethodFactory.REPAIR_METHOD createRepairMethod(Object[] value){//4
        return super.createRepairMethod(value);
   }
   
    @Override
   protected float createDestructionFactor(Object[] value){//7
       if(value != null) return (float) selectItemFromArray(value);
       int minValue = 0;
       int maxValue = DESTRUCTION_FACTOR_VALUES.length-1;
       int gapValue = 1;
       return DESTRUCTION_FACTOR_VALUES[RandomWrapper.createRandomNumber(minValue, maxValue, gapValue)];
   }
   
    @Override
   protected float createCoolingRate(boolean useSA, Object[] value){//1999
       if(!useSA)return -1;
       if(value != null) return (float) selectItemFromArray(value);
       int minValue = 0;
       int maxValue = COOLING_RATE_VALUES.length-1;
       int gapValue = 1;
       return COOLING_RATE_VALUES[RandomWrapper.createRandomNumber(minValue, maxValue, gapValue)];
   }
   
    @Override
   protected float createInitialTemperatureRatio(boolean useSA, Object[] value){//89
       if(!useSA)return -1;
       if(value != null) return (float) selectItemFromArray(value);
       int minValue = 0;
       int maxValue = INITIAL_TEMPERATURE_RATIO_VALUES.length-1;
       int gapValue = 1;
       return INITIAL_TEMPERATURE_RATIO_VALUES[RandomWrapper.createRandomNumber(minValue, maxValue, gapValue)];
   }
}

