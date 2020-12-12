package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.configuration;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveAglomerative;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveAglomerativeMQ;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.construtive.ConstrutiveDivisive;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.TabuSearch;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.TabuSearchLockCluster;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.TabuSearchMovimentSingleWay;
import br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts.TabuSearchMovimentTwoWay;

public class TabuConfiguration {
	
    public static enum ALGORITHM {
        C1, C2, C3, RAND;

        public String algorithmName(){
            switch (this) {
                case C1: return ConstrutiveAglomerative.NAME;
                case C2: return ConstrutiveAglomerativeMQ.NAME;
                case C3: return ConstrutiveDivisive.NAME;
                case RAND: return "R";
                default: throw new RuntimeException("UNKNOWN ALGORITHM!");
            }
        }
    };
        
    public static enum TABU_TYPE{
        LOCK_CLUSTER, MOVIMENT_SINGLE_WAY, MOVIMENT_TWO_WAY;
        
        public TabuSearch createTabuSearch(ResultWriter out, String suffix){
            switch(this){
                case LOCK_CLUSTER: return new TabuSearchLockCluster( out, suffix);
                case MOVIMENT_SINGLE_WAY: return new TabuSearchMovimentSingleWay(out, suffix);
                case MOVIMENT_TWO_WAY: return new TabuSearchMovimentTwoWay(out, suffix);
                default: throw new RuntimeException("UNKNOWN TABU!");
            }
        }
    };
        
    private long minSize;
    private long maxSize;
    private long aspiration;
    private long nrIterations;
    private int pathRelinkInterval;
    private TabuSearch tabuSearch;
    private int restarts;
	
    public TabuConfiguration(ResultWriter out, TABU_TYPE tabuSearch, long minSize, long maxSize, long aspiration, long nrIterations, int pathRelinkInterval, int restarts){
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.aspiration = aspiration;
        this.nrIterations = nrIterations;
        this.pathRelinkInterval = pathRelinkInterval;
        this.tabuSearch = tabuSearch.createTabuSearch(out, "");
        this.restarts = restarts;
    }

    @Override
    public String toString(){
        return 
            this.minSize+"_"+
            this.maxSize+"_"+
            this.aspiration+"_"+
            this.nrIterations+"_"+
            this.pathRelinkInterval+"_"+
            this.restarts;
    }

    public long getMinSize() {
        return minSize;
    }

    public void setMinSize(long minSize) {
        this.minSize = minSize;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getAspiration() {
        return aspiration;
    }

    public void setAspiration(long aspiration) {
        this.aspiration = aspiration;
    }

    public long getNrIterations() {
        return nrIterations;
    }

    public void setNrIterations(long nrIterations) {
        this.nrIterations = nrIterations;
    }

    public int getPathRelinkInterval() {
        return pathRelinkInterval;
    }

    public void setPathRelinkInterval(int pathRelinkInterval) {
        this.pathRelinkInterval = pathRelinkInterval;
    }

    public TabuSearch getTabuSearch() {
        return tabuSearch;
    }

    public void setTabuSearch(TabuSearch tabuSearch) {
        this.tabuSearch = tabuSearch;
    }

    public int getRestarts() {
        return restarts;
    }

    public void setRestarts(int restarts) {
        this.restarts = restarts;
    }
        
}
