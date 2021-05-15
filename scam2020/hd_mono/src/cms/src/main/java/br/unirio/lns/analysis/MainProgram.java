package br.unirio.lns.analysis;

import java.text.DecimalFormat;
import java.util.Vector;

import br.unirio.lns.analysis.experiment.ExperimentInstance;
import br.unirio.lns.analysis.experiment.ExperimentResult;
import br.unirio.lns.analysis.experiment.ParetoFrontier;
import br.unirio.lns.analysis.reader.ExperimentFileReader;
import br.unirio.lns.analysis.reader.ExperimentFileReaderException;

public class MainProgram
{
	private static final String TAB = "\t";
	
	private String getMask(int decimals)
	{
		String mask = "0";
		
		if (decimals > 0)
			mask += ".";
		
		for (int i = 0; i < decimals; i++)
			mask += "0";
		
		return mask;
	}

	private String getResultNames(Vector<ExperimentResult> results)
	{
		String s = "";
		
		for (int j = 0; j < results.size(); j++)
		{
			ExperimentResult result = results.elementAt(j);
			s += TAB + result.getName();
		}

		return s;
	}

	private String getResultCrossNames(Vector<ExperimentResult> results)
	{
		String s = "";
		
		for (int j1 = 0; j1 < results.size(); j1++)
		{
			ExperimentResult result1 = results.elementAt(j1);

			for (int j2 = j1+1; j2 < results.size(); j2++)
			{
				ExperimentResult result2 = results.elementAt(j2);
				s += TAB + result1.getName() + " x " + result2.getName();
			}
		}
		
		return s;
	}

	private String getInstanceName(int instanceNumber, Vector<ExperimentResult> results)
	{
		ExperimentResult result = results.elementAt(0);
		ExperimentInstance instance = result.getInstanceIndex(instanceNumber);
		return instance.getName();
	}

	private void analyzeInstance(int instanceNumber, Vector<ParetoFrontier> bestFrontiers, Vector<ExperimentResult> results, InstanceDataCollector collector, int decimals)
	{
		DecimalFormat dc = new DecimalFormat(getMask(decimals));
		Calculator c = new Calculator();

		System.out.print(getInstanceName(instanceNumber, results) + "\t");
		ParetoFrontier bestFrontier = bestFrontiers.elementAt(instanceNumber);

		for (int j = 0; j < results.size(); j++)
		{
			ExperimentResult result = results.elementAt(j);
			ExperimentInstance instance = result.getInstanceIndex(instanceNumber);
			double[] data = collector.collect(bestFrontier, instance);
			System.out.print(dc.format(c.calculateAverage(data)) + " + " + dc.format(c.calculateStandardDeviation(data)) + TAB);
		}

		System.out.print(TAB);

		for (int j1 = 0; j1 < results.size(); j1++)
		{
			ExperimentResult result1 = results.elementAt(j1);
			ExperimentInstance instance1 = result1.getInstanceIndex(instanceNumber);
			double[] data1 = collector.collect(bestFrontier, instance1);
			
			for (int j2 = j1+1; j2 < results.size(); j2++)
			{
				ExperimentResult result2 = results.elementAt(j2);
				ExperimentInstance instance = result2.getInstanceIndex(instanceNumber);
				double[] data2 = collector.collect(bestFrontier, instance);

				String stat = c.calculateMannWhitney(data1, data2) ? "EQ" : "DIFF";
				System.out.print(stat + TAB);
			}
		}

		System.out.println();
	}

	private void analyzeResults(Vector<ParetoFrontier> bestFrontiers, Vector<ExperimentResult> results, InstanceDataCollector collector, int decimals, int instanceCount, int cycleCount)
	{
		System.out.println(collector.getName() + getResultNames(results) + TAB + getResultCrossNames(results));

		for (int i = 0; i < instanceCount; i++)
			analyzeInstance(i, bestFrontiers, results, collector, decimals);
		
		System.out.println();
	}

	private void analyzeJointFrontiers(Vector<ParetoFrontier> bestFrontiers, Vector<ExperimentResult> results, int instanceCount)
	{
		System.out.print("Joint Frontier");
		
		for (int i = 0; i < results.size(); i++)
			System.out.print("\t" + results.elementAt(i).getName());

		System.out.println();

		for (int j = 0; j < instanceCount; j++)
		{
			ParetoFrontier bestFrontier = bestFrontiers.elementAt(j);
			System.out.print(getInstanceName(j, results) + ": " + bestFrontier.getVertexCount() + TAB);
			
			for (int i = 0; i < results.size(); i++)
			{
				ExperimentResult result = results.elementAt(i);
				ExperimentInstance instance = result.getInstanceIndex(j);
				System.out.print(bestFrontier.countCommons(instance.getFrontier()) + "\\" + instance.getFrontier().getVertexCount() + TAB);
			}

			System.out.println();
		}

		System.out.println();
	}

	private Vector<ExperimentResult> loadExperimentResults(String[] paths, int instanceCount, int cycleCount, int objectiveCount) throws ExperimentFileReaderException
	{
		Vector<ExperimentResult> results = new Vector<ExperimentResult>();
		ExperimentFileReader reader = new ExperimentFileReader();		
		
		for (int i = 0; i < paths.length; i++)
		{
			ExperimentResult result = reader.execute(paths[i], instanceCount, cycleCount, objectiveCount);
			results.add(result);
		}

		return results;
	}

	private Vector<ParetoFrontier> buildBestFrontiers(int instanceCount, int objectiveCount, Vector<ExperimentResult> results)
	{
		Vector<ParetoFrontier> bestFrontiers = new Vector<ParetoFrontier>();
		
		for (int i = 0; i < instanceCount; i++)
			bestFrontiers.add(new ParetoFrontier(objectiveCount));

		for (int i = 0; i < results.size(); i++)
		{
			ExperimentResult result = results.elementAt(i);

			for (int j = 0; j < instanceCount; j++)
			{
				ParetoFrontier frontier = bestFrontiers.elementAt(j);
				frontier.merge(result.getInstanceIndex(j).getFrontier());
			}
		}

		return bestFrontiers;
	}

	private void analyze(String[] paths, int instanceCount, int cycleCount, int objectiveCount) throws ExperimentFileReaderException
	{
		Vector<ExperimentResult> results = loadExperimentResults(paths, instanceCount, cycleCount, objectiveCount);
		Vector<ParetoFrontier> bestFrontiers = buildBestFrontiers(instanceCount, objectiveCount, results);

		analyzeJointFrontiers(bestFrontiers, results, instanceCount);
		analyzeResults(bestFrontiers, results, new InstanceDataCollectorExecutionTime(), 0, instanceCount, cycleCount);
		analyzeResults(bestFrontiers, results, new InstanceDataCollectorSolutionCount(), 1, instanceCount, cycleCount);
		analyzeResults(bestFrontiers, results, new InstanceDataCollectorBestSolutions(), 1, instanceCount, cycleCount);
		analyzeResults(bestFrontiers, results, new InstanceDataCollectorSpread(), 2, instanceCount, cycleCount);
		analyzeResults(bestFrontiers, results, new InstanceDataCollectorHypervolume(), 2, instanceCount, cycleCount);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws ExperimentFileReaderException
	{
		String[] paths50C =
		{
			"results\\50C\\MQ.txt",
			"results\\50C\\NONE.txt"/*, 
			"results\\50C\\CdH.txt",
			"results\\50C\\HdHC.txt",
			"results\\50C\\CH.txt",
			"results\\50C\\CH3.txt"*/
		};
		
		String[] paths100C =
		{
			"results\\100C\\MQ.txt",
			"results\\100C\\NONE.txt"
		};
		
		String[] paths150C =
		{
			"results\\150C\\MQ.txt",
			"results\\150C\\NONE.txt"
		};
		
		String[] paths200C =
		{
			"results\\200C\\MQ.txt",
			"results\\200C\\NONE.txt"
		};
		
		String[] pathsReal =
		{
			"results\\Real\\MQ.txt",
			"results\\Real\\NONE.txt"
		};
		
		new MainProgram().analyze(pathsReal, 11, 20, 3);
	}
}

abstract class InstanceDataCollector
{
	private String name;
	
	public InstanceDataCollector(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public abstract double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance);
}

class InstanceDataCollectorExecutionTime extends InstanceDataCollector
{
	public InstanceDataCollectorExecutionTime()
	{
		super("Instance Name");
	}

	@Override
	public double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance)
	{
		return instance.getExecutionTimes();
	}
}

class InstanceDataCollectorSolutionCount extends InstanceDataCollector
{
	public InstanceDataCollectorSolutionCount()
	{
		super("Solution Count");
	}

	@Override
	public double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance)
	{
		return instance.getSolutionCount();
	}
}

class InstanceDataCollectorBestSolutions extends InstanceDataCollector
{
	public InstanceDataCollectorBestSolutions()
	{
		super("Best Solutions");
	}

	@Override
	public double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance)
	{
		return instance.getBestSolutions(bestFrontier);
	}
}

class InstanceDataCollectorSpread extends InstanceDataCollector
{
	public InstanceDataCollectorSpread()
	{
		super("Spread");
	}

	@Override
	public double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance)
	{
		return instance.getSpreads(bestFrontier);
	}
}

class InstanceDataCollectorHypervolume extends InstanceDataCollector
{
	public InstanceDataCollectorHypervolume()
	{
		super("Hypervolume");
	}

	@Override
	public double[] collect(ParetoFrontier bestFrontier, ExperimentInstance instance)
	{
		return instance.getHypervolumes(bestFrontier);
	}
}