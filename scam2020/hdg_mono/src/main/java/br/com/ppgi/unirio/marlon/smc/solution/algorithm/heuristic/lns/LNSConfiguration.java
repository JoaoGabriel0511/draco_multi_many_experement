package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.mdg.ClusterMetrics;
import br.com.ppgi.unirio.marlon.smc.mdg.ModuleDependencyGraph;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.AConstrutiveSolutionBuilder;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.InitialSolutionFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ADestroySolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.ARepairSolution;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.DestrutiveMethodFactory;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.lns.methods.builder.RepairMethodFactory;
import java.io.IOException;
import random.number.generator.RandomWrapper;

public class LNSConfiguration{
    protected static final String SEPARATOR = "_";
    protected ResultWriter iterationOut;
    
    protected static enum InitialSolutionAlgorithm {RANDOM, GREEDY_BY_MQ};
    
    protected final ModuleDependencyGraph mdg;
    protected ClusterMetrics bestSolution;
    
    protected boolean useSA = false;
    protected int iterationLimit = 3000;
    protected int timeLimit = -1;
    protected int noImprovementLimit = -1;
    protected int algorithNoImprovementLimit = -1;
    
    protected float destructionFactor = .4f;
    
    protected boolean mixedRestart = true;
    protected double reductionFactor = 1;
    
    //fator de destruição multiplo
    private class MultipleDestructionFactor{
        protected float[] destructionFactors;
        protected int[] destructionFactorsTickets;
        private int totalTickets=0;
        public MultipleDestructionFactor(float[] destructionFactors, int[] destructionFactorsTickets){
            this.destructionFactors = destructionFactors;
            this.destructionFactorsTickets = destructionFactorsTickets;
            
            
            for (int destructionFactorsTicket : destructionFactorsTickets) {
                totalTickets += destructionFactorsTicket;
            }
        }
        
        
        public float raffleNextDestructionFaction(){
            int r = RandomWrapper.createRandomNumber(1, totalTickets, 1);//cria um numero inteiro entre o minimo e o maximo
            
            //verifica qual indice possui o ticket
            int ticket=0;
            for (int i=0;i< destructionFactorsTickets.length;i++) {
                ticket += destructionFactorsTickets[i];
                if(ticket>=r){
                    return destructionFactors[i];
                }
            }
            throw new RuntimeException("VALOR DE DESTRUICTION FACTOR NÃO PODE SER SORTEADO!");
        }
        
        @Override 
        public String toString(){
            StringBuilder sb = new StringBuilder("[");
            for (int i=0;i<this.destructionFactors.length;i++){
                if(i>0){
                    sb.append("/");
                }
                sb.append(this.destructionFactors[i]);
                sb.append("-");
                sb.append(this.destructionFactorsTickets[i]);
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    private MultipleDestructionFactor multipleDestructionFactor = null;
    
    protected float coolingRate = 0.99975f; //fator multiplicador da temperatura
    protected float inicialTemperatureRatio = 0.05f; //ajusta a temperatura inicial em função da solução inicial gerada. Uma solução que seja esse percentual pior que a inicial terã 50% de chance de ser aceita na primeira iteração
    
    protected InitialSolutionFactory.CREATION_METHOD initialMethod = InitialSolutionFactory.CREATION_METHOD.CAMQ;
    protected DestrutiveMethodFactory.DESTRUTIVE_METHOD destrutiveMethod = DestrutiveMethodFactory.DESTRUTIVE_METHOD.DR;
    protected RepairMethodFactory.REPAIR_METHOD repairMethod = RepairMethodFactory.REPAIR_METHOD.RGBIR;
    protected RepairMethodFactory.REPAIR_METHOD[] repairMethods = null;
    private int currentRepairMethod=0;
    
    private ADestroySolution destroyAlgorithm;
    private ARepairSolution repairAlgorithm;
    private ARepairSolution[] repairAlgorithms;
    private AConstrutiveSolutionBuilder initialSolutionBuilder;

    public LNSConfiguration (ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName ){
        this.mdg = mdg;
        if(mdg == null) return;
        if(fileName == null){
            fileName = "";
        }
        open("LNS",outputTo, outPath, fileName);
    }
    
    protected LNSConfiguration (ModuleDependencyGraph mdg, ResultWriter.OUTPUT outputTo, String outPath, String fileName, String preName){
        this.mdg = mdg;
        if(mdg == null) return;
        if(fileName == null){
            fileName = "";
        }
        open(preName,outputTo, outPath, fileName);
    }

    protected final void open(String preName, ResultWriter.OUTPUT outputTo, String outPath, String fileName){
        if(outputTo==null){
            return;
        }
        try{
            this.iterationOut = ResultWriter.configureResultWriter(outputTo,outPath, preName+SEPARATOR+mdg.getName()+SEPARATOR+"all"+SEPARATOR+fileName);
        }catch(IOException e){
            throw new RuntimeException("ERRO CRIANDO ARQUIVOS DE SAIDA");
        }
    }
    
    public void close(){
        if(this.iterationOut == null){
            return;
        }
        this.iterationOut.close();
    }
    
    public LNSConfiguration configure(boolean useSA, int iterationLimit,int timeLimit, int noImprovementLimit, int algorithNoImprovementLimit
            , float[] destructionFactor, int[] destructionFactorTickes, double reductionFactor, boolean mixedRestart
            , float coolingRate, float inicialTemperatureRatio
            , InitialSolutionFactory.CREATION_METHOD initialMethod, DestrutiveMethodFactory.DESTRUTIVE_METHOD destrutiveMethod
            , RepairMethodFactory.REPAIR_METHOD[] repairMethods){
        
        this.multipleDestructionFactor = new MultipleDestructionFactor(destructionFactor, destructionFactorTickes);
        this.algorithNoImprovementLimit = algorithNoImprovementLimit;
        this.currentRepairMethod = 0;
        this.initialMethod = initialMethod;
        this.destrutiveMethod = destrutiveMethod;
        this.repairMethods = repairMethods;
        this.mixedRestart = mixedRestart;
        this.reductionFactor = reductionFactor;
        
        this.configure(useSA, iterationLimit, timeLimit, noImprovementLimit, coolingRate, inicialTemperatureRatio);
        return this;
    }
    
    
    public LNSConfiguration configure(boolean useSA, int iterationLimit,int timeLimit, int noImprovementLimit
            , float destructionFactor, float coolingRate
            , float inicialTemperatureRatio
            , InitialSolutionFactory.CREATION_METHOD initialMethod, DestrutiveMethodFactory.DESTRUTIVE_METHOD destrutiveMethod
            , RepairMethodFactory.REPAIR_METHOD repairMethod){
        
        this.initialMethod = initialMethod;
        this.destrutiveMethod = destrutiveMethod;
        this.repairMethod = repairMethod;
        
        this.configure(useSA, iterationLimit, timeLimit, noImprovementLimit, destructionFactor, coolingRate, inicialTemperatureRatio);
        return this;
    }

      
    protected LNSConfiguration configure(boolean useSA, int iterationLimit,int timeLimit, int noImprovementLimit
            , float destructionFactor, float coolingRate
            , float inicialTemperatureRatio){
        
        this.destructionFactor = destructionFactor;
        
        configure(useSA, iterationLimit, timeLimit, noImprovementLimit, coolingRate, inicialTemperatureRatio);
        return this;
    }
    
    private LNSConfiguration configure(boolean useSA, int iterationLimit,int timeLimit, int noImprovementLimit
            , float coolingRate
            , float inicialTemperatureRatio){
        
        this.useSA = useSA;
        this.iterationLimit = iterationLimit;
        this.timeLimit = timeLimit;
        this.noImprovementLimit = noImprovementLimit;
        this.coolingRate = coolingRate;
        this.inicialTemperatureRatio = inicialTemperatureRatio;
        
        buildConfiguration();
        return this;
    }
    
    
    public LNSConfiguration buildConfiguration(){
        initialSolutionBuilder = InitialSolutionFactory.build(initialMethod);
        destroyAlgorithm = DestrutiveMethodFactory.build(destrutiveMethod, this);
        
        if(repairMethods != null){
            repairAlgorithms = new ARepairSolution[repairMethods.length];
            for (int i=0;i< repairMethods.length;i++) {
                repairAlgorithms[i] = RepairMethodFactory.build(repairMethods[i], this);
            }
            repairAlgorithm = repairAlgorithms[this.currentRepairMethod];
        }else{
            repairAlgorithm=RepairMethodFactory.build(repairMethod, this);
        }
        
        
        return this;
    }
    
    
    public void restart(){
        if(repairAlgorithms != null){
            currentRepairMethod = 0;
            repairAlgorithm = repairAlgorithms[this.currentRepairMethod];
        }
    }
    
    /**
     * altera o valor do fator de destruição se estiver configurado para tal
     */
    public void changeDestructionFactor(){
        if(multipleDestructionFactor != null){
            this.destructionFactor=multipleDestructionFactor.raffleNextDestructionFaction();
        }
    }
    
    public int changeRepairMethod(){
        if(this.currentRepairMethod+1 < this.repairMethods.length){
            this.currentRepairMethod++;
        }else{
            this.currentRepairMethod=0;
        }
        
        this.repairMethod = this.repairMethods[currentRepairMethod];
        this.repairAlgorithm = this.repairAlgorithms[currentRepairMethod];
        return this.currentRepairMethod;
    }
    
    @Override
    public String toString(){
        return useSA?"COM-SA":"SEM-SA"
            +SEPARATOR+iterationLimit
            +SEPARATOR+timeLimit
            +SEPARATOR+noImprovementLimit
            +SEPARATOR+(multipleDestructionFactor == null ? destructionFactor : multipleDestructionFactor.toString())
            +(useSA?
                    SEPARATOR+coolingRate
                    +SEPARATOR+inicialTemperatureRatio
                    :"")
            +SEPARATOR+initialSolutionBuilder.getName()
            +SEPARATOR+destroyAlgorithm.NAME
            +SEPARATOR+repairAlgorithmName()
                ;
    }
    
    private String repairAlgorithmName(){
        if(repairMethods == null){
            return repairAlgorithm.NAME;
        }else{
            StringBuilder sb = new StringBuilder("[");
            
            for(int i=0;i<repairAlgorithms.length;i++){
                if(i>0){
                    sb.append(",");
                }
                sb.append(repairAlgorithms[i].NAME);
            }
            sb.append("]");
            sb.append(SEPARATOR);
            if(repairAlgorithms.length>1){
                if(mixedRestart){
                    sb.append("RESTART-ENABLED");
                }
                sb.append("RESTART-DISABLED");
            }
            return sb.toString();
        }
    }
    
    /**
     * Escreve uma linha no relatório de saida
     * @param cm
     * @param maxMQ
     * @param bestSolutionIteration
     * @param iterationN
     * @param time
     * @param algorithmName 
     */
    public void writeIterationReport(ClusterMetrics cm,double maxMQ, long bestSolutionIteration, long iterationN, long time, String algorithmName){
        if(iterationOut == null) return;
        iterationOut.writeLine(cm, maxMQ, bestSolutionIteration, iterationN, time, algorithmName, this.toString());
    }
    
    public void setBestSolution(ClusterMetrics bestSolution) {
        this.bestSolution = bestSolution;
    }
    
    
    
    
    
    
    
    
    
    

    public ResultWriter getOut() {
        return iterationOut;
    }

    public ResultWriter getIterationOut() {
        return iterationOut;
    }

    public ModuleDependencyGraph getMdg() {
        return mdg;
    }

    public ClusterMetrics getBestSolution() {
        return bestSolution;
    }

    public int getIterationLimit() {
        return iterationLimit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public float getDestructionFactor() {
        return destructionFactor;
    }

    public float getCoolingRate() {
        return coolingRate;
    }

    public float getInicialTemperatureRatio() {
        return inicialTemperatureRatio;
    }

   

    public ADestroySolution getDestroyAlgorithm() {
        return destroyAlgorithm;
    }

    public ARepairSolution getRepairAlgorithm() {
        return repairAlgorithm;
    }

    public AConstrutiveSolutionBuilder getInitialSolutionBuilder() {
        return initialSolutionBuilder;
    }

    public int getNoImprovementLimit() {
        return noImprovementLimit;
    }

    public boolean getUseSA() {
        return useSA;
    }

    public void setUseSA(boolean useSA) {
        this.useSA = useSA;
    }
    
    
    
    
    
}