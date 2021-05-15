package br.unirio.lns.conflict;

import java.text.DecimalFormat;
import java.util.Vector;

import jmetal.util.PseudoRandom;
import br.unirio.lns.hdesign.calculator.CouplingCalculator;
import br.unirio.lns.hdesign.model.Project;

public class ConflictCalculator
{
	private static final String TAB = "\t";
	private static final int SIMULATIONS = 10000;
	private static final int STATUS_COMPARABLE = 0;
	private static final int STATUS_1WEAKDOM2 = 1;
	private static final int STATUS_2WEAKDOM1 = 2;
	private static final int STATUS_INCOMPARABLE = 3;
	
	private int calculateStatus(double[] objectiveValues1, double[] objectiveValues2, int[] objectives)
	{
		int status = 0;
		
		for (int i = 0; i < objectives.length; i++)
		{
			int objective = objectives[i] - 1;
			
			if (objectiveValues1[objective] < objectiveValues2[objective])
			{
				if (status == STATUS_COMPARABLE)
					status = STATUS_1WEAKDOM2;

				else if (status == STATUS_2WEAKDOM1)
					status = STATUS_INCOMPARABLE;
			}
			else if (objectiveValues2[objective] < objectiveValues1[objective])
			{
				if (status == STATUS_COMPARABLE)
					status = STATUS_2WEAKDOM1;

				else if (status == STATUS_1WEAKDOM2)
					status = STATUS_INCOMPARABLE;
			}
		}
		
		return status;
	}
	
	private double calculateDistanceFirstToSecond(double[] objectiveValues1, double[] objectiveValues2, int[] objectives)
	{
		double distance = 0.0;
		
		for (int i = 0; i < objectives.length; i++)
		{
			int objective = objectives[i] - 1;
			
			if (objectiveValues1[objective] < objectiveValues2[objective])
				distance = Math.max(distance, Math.abs(objectiveValues1[objective] - objectiveValues2[objective]));
		}
		
		return distance;
	}
	
	private double calculateDistanceSecondToFirst(double[] objectiveValues1, double[] objectiveValues2, int[] objectives)
	{
		double distance = 0.0;
		
		for (int i = 0; i < objectives.length; i++)
		{
			int objective = objectives[i] - 1;
			
			if (objectiveValues2[objective] < objectiveValues1[objective])
				distance = Math.max(distance, Math.abs(objectiveValues1[objective] - objectiveValues2[objective]));
		}
		
		return distance;
	}
	
	private int[] createAllObjectives(int objectiveCount)
	{
		int[] allObjectives = new int[objectiveCount];
		
		for (int i = 0; i < objectiveCount; i++)
			allObjectives[i] = i + 1;
		
		return allObjectives;
	}

	private int[] createMissingObjectives(int[] objectiveSubset, int objectiveCount)
	{
		int[] missingObjectives = new int[objectiveCount - objectiveSubset.length];
		int walker = 0;
		
		for (int i = 0; i < objectiveCount; i++)
		{
			boolean found = false;
			
			for (int j = 0; j < objectiveSubset.length && !found; j++)
				if (objectiveSubset[j] == i+1)
					found = true;
			
			if (!found)
				missingObjectives[walker++] = i + 1;
		}
		return missingObjectives;
	}

	public ConflictMeasurement measureConflict(double[][] objectiveValues, int[] objectiveSubset) throws Exception
	{
		int objectiveCount = objectiveValues[0].length;
		int instanceCount = objectiveValues.length;
		
		int[] allObjectives = createAllObjectives(objectiveCount);
		int[] missingObjectives = createMissingObjectives(objectiveSubset, objectiveCount);
		
		double sigma = 0;
		int occurrences = 0;

		for (int i = 0; i < instanceCount; i++)
			for (int j = i+1; j < instanceCount; j++)
			{
				int statusAllObjectives = calculateStatus(objectiveValues[i], objectiveValues[j], allObjectives);
				int statusSelectedObjectives = calculateStatus(objectiveValues[i], objectiveValues[j], objectiveSubset);

				if (statusAllObjectives == STATUS_INCOMPARABLE && statusSelectedObjectives == STATUS_1WEAKDOM2)
				{
					sigma = Math.max (sigma, calculateDistanceSecondToFirst(objectiveValues[i], objectiveValues[j], missingObjectives));
					occurrences++;
				}
				
				else if (statusAllObjectives == STATUS_INCOMPARABLE && statusSelectedObjectives == STATUS_2WEAKDOM1)
				{
					sigma = Math.max (sigma, calculateDistanceFirstToSecond(objectiveValues[i], objectiveValues[j], missingObjectives));
					occurrences++;
				}

				else if (statusAllObjectives == STATUS_INCOMPARABLE && statusSelectedObjectives == STATUS_COMPARABLE)
				{
					sigma = Math.max (sigma, 0.001);
					occurrences++;
				}

				else if (statusAllObjectives == STATUS_COMPARABLE && statusSelectedObjectives == STATUS_1WEAKDOM2)
					throw new Exception ("COMP -> 1W2");
				
				else if (statusAllObjectives == STATUS_COMPARABLE && statusSelectedObjectives == STATUS_2WEAKDOM1)
					throw new Exception ("COMP -> 2W1");

				else if (statusAllObjectives == STATUS_COMPARABLE && statusSelectedObjectives == STATUS_INCOMPARABLE)
					throw new Exception ("COMP -> INCOMP");

				//if (statusAllObjectives == STATUS_1WEAKDOM2 && statusSelectedObjectives == STATUS_COMPARABLE)
				//	throw new Exception ("1W2 -> COMP");
				
				else if (statusAllObjectives == STATUS_1WEAKDOM2 && statusSelectedObjectives == STATUS_2WEAKDOM1)
					throw new Exception ("1W2 -> 2W1");

				else if (statusAllObjectives == STATUS_1WEAKDOM2 && statusSelectedObjectives == STATUS_INCOMPARABLE)
					throw new Exception ("1W2 -> INCOMP");

				//if (statusAllObjectives == STATUS_2WEAKDOM1 && statusSelectedObjectives == STATUS_COMPARABLE)
				//	throw new Exception ("2W1 -> COMP");
				
				else if (statusAllObjectives == STATUS_2WEAKDOM1 && statusSelectedObjectives == STATUS_1WEAKDOM2)
					throw new Exception ("2W1 -> 1W2");

				else if (statusAllObjectives == STATUS_2WEAKDOM1 && statusSelectedObjectives == STATUS_INCOMPARABLE)
					throw new Exception ("2W1 -> INCOMP");
			}
		
		return new ConflictMeasurement(occurrences, sigma);
	}

	private void shuffleClasses(Project project, CouplingCalculator calculator) throws Exception
	{
		calculator.reset();
		int packageCount = project.getPackageCount();
		int classCount = project.getClassCount();
		int moveCount = PseudoRandom.randInt(0, classCount-1);
		
		for (int i = 0; i < moveCount; i++)
		{
			int classIndex = PseudoRandom.randInt(0, classCount-1);
			int packageIndex = PseudoRandom.randInt(0, packageCount-1);
			calculator.moveClass(classIndex, packageIndex);
		}
	}
	
	public void executeSimulation(Project project) throws Exception
	{
		CouplingCalculator calculator = new CouplingCalculator(project);
		double[][] instances = new double[SIMULATIONS][4];
	
		for (int i = 0; i < SIMULATIONS; i++)
		{
			instances[i][0] = -calculator.calculateCohesion();
			instances[i][1] = calculator.calculateCoupling();
			instances[i][2] = calculator.calculateDifference();
			instances[i][3] = -calculator.calculateModularizationQuality();
			shuffleClasses(project, calculator);
		}

		ConflictMeasurement cm = measureConflict(instances, new int[] { 1, 2, 3 });
		double comparisons = SIMULATIONS * (SIMULATIONS - 1) / 2.0;
		double percentile = cm.getOccurrences() * 100.0 / comparisons;

		DecimalFormat dc = new DecimalFormat("0.00");
		System.out.println(project.getName() + TAB + dc.format(cm.getSigma()) + TAB + dc.format(percentile));
	}
	
	public void execute(Vector<Project> projects) throws Exception
	{
		for (int i = 0; i < projects.size(); i++)
			executeSimulation(projects.elementAt(i));
	}
}