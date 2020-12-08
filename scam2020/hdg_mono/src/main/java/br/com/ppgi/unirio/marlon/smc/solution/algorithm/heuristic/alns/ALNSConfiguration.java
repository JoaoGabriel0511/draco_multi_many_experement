package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.alns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.LNSConfiguration;

public class ALNSConfiguration extends LNSConfiguration{
    private float HCThreshold = 0.8f; //quantidade de vizinhos que o HC olha antes de parar e retornar
    
    private int refreshScoreInterval = 100;
    private float weightBSF = 4f; //BestSolutionFound
    private float weightBTC = 2f; //BetterThanCurrent
    private float weightWTCA = 1f; //WorseThanCurrentAccepted
    private float weightWTCNA = 0f; //WorseThanCurrentNotAccepted
    private float reactionFactor = 0.1f; //fator de reação. Quao sensível será a mudança de peso ao longo do tempo

    public ALNSConfiguration (ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName ){
        super(mdg, outputTo, outPath, fileName);
        super.open("LNS",outputTo, outPath, fileName);
        
    }
   
    
    public ALNSConfiguration configure(boolean useSA, int iterationLimit,int timeLimit, int noImprovementLimit
            , float destructionFactor, float coolingRate
            , float inicialTemperatureRatio, float HCThreshold
            , int refreshScoreInterval, float weightBSF
            , float weightBTC, float weightWTCA
            , float weightWTCNA, float reactionFactor){
        
        super.configure(useSA, iterationLimit, timeLimit,noImprovementLimit, destructionFactor, coolingRate, inicialTemperatureRatio);
        
        this.HCThreshold = HCThreshold;
        this.refreshScoreInterval = refreshScoreInterval;
        this.weightBSF = weightBSF;
        this.weightBTC = weightBTC;
        this.weightWTCA = weightWTCA;
        this.weightWTCNA = weightWTCNA;
        this.reactionFactor = reactionFactor;
        
        return this;
    }
    
    @Override
    public String toString(){
        return super.toString()
            +SEPARATOR+HCThreshold
            +SEPARATOR+refreshScoreInterval
            +SEPARATOR+weightBSF
            +SEPARATOR+weightBTC
            +SEPARATOR+weightWTCA
            +SEPARATOR+weightWTCNA
            +SEPARATOR+reactionFactor
        ;
    }
    
    public float getHCThreshold() {
        return HCThreshold;
    }

    public int getRefreshScoreInterval() {
        return refreshScoreInterval;
    }    

    public float getWeightBSF() {
        return weightBSF;
    }

    public float getWeightBTC() {
        return weightBTC;
    }

    public float getWeightWTCA() {
        return weightWTCA;
    }

    public float getWeightWTCNA() {
        return weightWTCNA;
    }

    public float getReactionFactor() {
        return reactionFactor;
    }    
}

