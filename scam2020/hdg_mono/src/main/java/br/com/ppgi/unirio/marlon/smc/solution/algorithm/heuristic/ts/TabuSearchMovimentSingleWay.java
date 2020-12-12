package br.com.ppgi.unirio.marlon.smc.solution.algorithm.heuristic.ts;

import br.com.ppgi.unirio.marlon.smc.experiment.output.ResultWriter;

public class TabuSearchMovimentSingleWay extends TabuSearch{
	
	private long[][] tabuList;
    
    public TabuSearchMovimentSingleWay(ResultWriter out,String suffix){
            super(out, suffix);
            NAME = "TabuSearchMovimentSingleWay";
	}
	
    @Override
    protected void resetTabuList(int n){
        tabuList = new long[n][n];
        for(int i=0;i<n;i++){for(int j=0;j<n;j++){tabuList[i][j] = 0;}}
    }

    @Override
    protected long checkTabuValue(int module, int cluster) {
        return tabuList[module][cluster];
    }

    @Override
    protected void addTabu(long iterationN, int module, int cluster, int modulePositionBefore) {
        tabuList[module][modulePositionBefore] = iterationN;
    }

}

