/**
 * gGA.java
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.Comparator;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.ObjectiveComparator;
import jmetal.util.JMException;

/**
 * Class implementing a generational genetic algorithm
 */
public class gGA extends Algorithm
{
	private Problem problem_;

	/**
	 * Constructor Create a new GGA instance.
	 * 
	 * @param problem Problem to solve.
	 */
	public gGA(Problem problem)
	{
		this.problem_ = problem;
	}

	/**
	 * Execute the GGA without a notifier
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		return execute(null);
	}

	/**
	 * Execute the GGA algorithm given a notifier
	 */
	public SolutionSet execute(GANotifier notifier) throws JMException, ClassNotFoundException
	{
		int populationSize;
		int maxEvaluations;
		int evaluations;

		SolutionSet population;
		SolutionSet offspringPopulation;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		// Single objective comparator
		Comparator<Solution> comparator = new ObjectiveComparator(0);

		// Read the params
		populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();

		// Initialize the variables
		population = new SolutionSet(populationSize);
		offspringPopulation = new SolutionSet(populationSize);

		evaluations = 0;

		// Read the operators
		mutationOperator = this.operators_.get("mutation");
		crossoverOperator = this.operators_.get("crossover");
		selectionOperator = this.operators_.get("selection");

		// Create the initial population
		Solution newIndividual;
		for (int i = 0; i < populationSize; i++)
		{
			newIndividual = new Solution(problem_);
			problem_.evaluate(newIndividual);
			evaluations++;
			population.add(newIndividual);
		}

		// Sort population
		population.sort(comparator);
		while (evaluations < maxEvaluations)
		{
			// Copy the best two individuals to the offspring population
			offspringPopulation.add(new Solution(population.get(0)));
			offspringPopulation.add(new Solution(population.get(1)));

			// Reproductive cycle
			for (int i = 0; i < (populationSize / 2 - 1); i++)
			{
				// Selection
				Solution[] parents = new Solution[2];
				parents[0] = (Solution) selectionOperator.execute(population);
				parents[1] = (Solution) selectionOperator.execute(population);

				// Crossover
				Solution[] offspring = (Solution[]) crossoverOperator.execute(parents);

				// Mutation
				mutationOperator.execute(offspring[0]);
				mutationOperator.execute(offspring[1]);

				// Evaluation of the new individual
				problem_.evaluate(offspring[0]);
				problem_.evaluate(offspring[1]);
				evaluations += 2;

				// Replacement: the two new individuals are inserted in the offspring population
				offspringPopulation.add(offspring[0]);
				offspringPopulation.add(offspring[1]);
			}

			// The offspring population becomes the new current population
			population.clear();

			for (int i = 0; i < populationSize; i++)
				population.add(offspringPopulation.get(i));

			offspringPopulation.clear();
			population.sort(comparator);

			if (notifier != null)
				notifier.newIteration(evaluations, population.get(0));
		}

		// Return a population with the best individual
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(population.get(0));
		return resultPopulation;
	}
}