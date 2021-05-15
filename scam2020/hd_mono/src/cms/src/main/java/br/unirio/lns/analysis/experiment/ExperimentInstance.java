package br.unirio.lns.analysis.experiment;

import java.util.Vector;

import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.Spread;

public class ExperimentInstance
{
	private Vector<ExperimentCycle> cycles;
	private ParetoFrontier bestFrontier;
	private int objectiveCount;
	private String name;
	
	public ExperimentInstance(int objectivesCount)
	{
		this.name = "";
		this.objectiveCount = objectivesCount;
		this.cycles = new Vector<ExperimentCycle>();
		this.bestFrontier = new ParetoFrontier(objectivesCount);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
	
	public ParetoFrontier getFrontier()
	{
		return bestFrontier;
	}
	
	public ExperimentCycle createCycle()
	{
		return new ExperimentCycle(objectiveCount);
	}
	
	public int getCycleCount()
	{
		return cycles.size();
	}
	
	public ExperimentCycle getCycleIndex(int index)
	{
		return cycles.elementAt(index);
	}
	
	public void addCycle(ExperimentCycle item)
	{
		cycles.add(item);
	}
	
	public void removeCycle(int index)
	{
		cycles.remove(index);
	}
	
	public ParetoFrontier calculateBestFrontier()
	{
		ParetoFrontier frontier = new ParetoFrontier(objectiveCount);
		
		for (int i = 0; i < cycles.size(); i++)
			frontier.merge(getCycleIndex(i).getFrontier());
		
		return frontier;
	}
	
	public double[] getExecutionTimes()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getExecutionTime();
		
		return results;
	}
	
	public double[] getSolutionCount()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getFrontier().getVertexCount();
		
		return results;
	}
	
	public double[] getBestSolutions(ParetoFrontier bestFrontier)
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = bestFrontier.countCommons(getCycleIndex(i).getFrontier());
		
		return results;
	}
	
	public double[] getSpreads(ParetoFrontier bestFrontier)
	{
		double[] results = new double[cycles.size()];
		double[][] bestFrontierValues = bestFrontier.getValues();
		Spread spread = new Spread();
		
		for (int i = 0; i < cycles.size(); i++)
		{
			ExperimentCycle cycle = getCycleIndex(i);
			double[][] currentFrontierValues = cycle.getFrontier().getValues();
			results[i] = spread.spread(currentFrontierValues, bestFrontierValues, bestFrontier.getObjectiveCount());
		}
		
		return results;
	}
	
	public double[] getHypervolumes(ParetoFrontier bestFrontier)
	{
		double[] results = new double[cycles.size()];
		double[][] bestFrontierValues = bestFrontier.getValues();
		Hypervolume hv = new Hypervolume();
		
		for (int i = 0; i < cycles.size(); i++)
		{
			ExperimentCycle cycle = getCycleIndex(i);
			double[][] currentFrontierValues = cycle.getFrontier().getValues();
			results[i] = hv.hypervolume(currentFrontierValues, bestFrontierValues, bestFrontier.getObjectiveCount());
		}
		
		return results;
	}
}