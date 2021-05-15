package jmetal.metaheuristics.randomSearch;

import java.util.Comparator;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.ObjectiveComparator;
import jmetal.util.JMException;

public class MonoRandomSearch extends Algorithm
{
	private Problem problem_;
	private int maxEvaluations;

	public MonoRandomSearch(Problem problem, int maxEvaluations)
	{
		this.problem_ = problem;
		this.maxEvaluations = maxEvaluations;
	}

	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		// Single objective comparator
		Comparator<Solution> comparator = new ObjectiveComparator(0);

		// Creates the first individual (current best)
		Solution bestIndividual = new Solution(problem_);
		problem_.evaluate(bestIndividual);
		int evaluations = 1;

		// Creates the walker solution
		Solution nextIndividual = new Solution(problem_);
		
		// Performs the required evaluations
		while (evaluations < maxEvaluations)
		{
			nextIndividual.randomize();
			problem_.evaluate(nextIndividual);
			evaluations++;

			if (comparator.compare(nextIndividual, bestIndividual) < 0)
			{
				bestIndividual = nextIndividual;
				nextIndividual = new Solution(problem_);
			}
		}

		// Return a population with the best individual
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(bestIndividual);
		return resultPopulation;
	}
}