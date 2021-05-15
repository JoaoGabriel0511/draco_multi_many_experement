package br.unirio.lns.analysis.experiment;

public class ExperimentCycle
{
	private ParetoFrontier frontier;
	private double executionTime;
	
	public ExperimentCycle(int objectivesCount)
	{
		this.frontier = new ParetoFrontier(objectivesCount);
		this.executionTime = 0.0;
	}
	
	public ParetoFrontier getFrontier()
	{
		return frontier;
	}
	
	public double getExecutionTime()
	{
		return executionTime;
	}
	
	public void setExecutionTime(double executionTime)
	{
		this.executionTime = executionTime;
	}
}