package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;

public class TabuSearchLockCluster extends TabuSearch{
	
	private long[] tabuList;
    
    public TabuSearchLockCluster(ResultWriter out,String suffix){
            super(out, suffix);
            NAME = "TabuSearchLockCluster";
	}
	
    @Override
    protected void resetTabuList(int n){
        tabuList = new long[n];
        for(int i=0;i<n;i++){tabuList[i] = 0;}
    }

    @Override
    protected long checkTabuValue(int module, int cluster) {
        return tabuList[module];
    }

    @Override
    protected void addTabu(long iterationN, int module, int cluster, int modulePositionBefore) {
        tabuList[module] = iterationN;
    }

}

