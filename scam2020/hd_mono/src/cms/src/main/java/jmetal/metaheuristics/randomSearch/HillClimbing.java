package jmetal.metaheuristics.randomSearch;

import java.util.Comparator;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.comparator.ObjectiveComparator;
import jmetal.base.visitor.neighborhood.BinaryNeighborVisitor;
import jmetal.util.JMException;

public class HillClimbing extends Algorithm
{
	private Problem problem;
	private BinaryNeighborVisitor visitor;
	private int maxEvaluations;
	private int totalEvaluationsWithNoBetterSolution;
	private boolean randomRestart;

	/**
	 * Para compatibilidade
	 * @param problem
	 * @param visitor
	 * @param maxEvaluations
	 */
	public HillClimbing(Problem problem, BinaryNeighborVisitor visitor, int maxEvaluations)
	{
		this.problem = problem;
		this.visitor = visitor;
		this.maxEvaluations = maxEvaluations;
		this.totalEvaluationsWithNoBetterSolution = 100;
		this.randomRestart = false;
	}
	
	/**
	 *  
	 */
	public HillClimbing(Problem problem, BinaryNeighborVisitor visitor, int maxEvaluations, int totalEvaluationsWithNoBetterSolution, boolean randomRestart)
	{
		this.problem = problem;
		this.visitor = visitor;
		this.maxEvaluations = maxEvaluations;
		this.totalEvaluationsWithNoBetterSolution = totalEvaluationsWithNoBetterSolution;
		this.randomRestart = randomRestart;
	}
	
	/**
	 * Executa a busca heurística HC
	 * @param randomRestart : determina se após o critério de parada utiliza um reiniciar aleatório, saindo de um ótimo local
	 * @return solutionset com a melhor solução encontrada
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		// Single objective comparator
		Comparator<Solution> comparator = new ObjectiveComparator(0);

		// Creates the first individual (current best)
		Solution bestIndividual = new Solution(problem);
		Solution lastBest = bestIndividual;
		Solution lastGlobalBest = bestIndividual;
		
		problem.evaluate(bestIndividual);
		int evaluations = 1;
		int findBetterEvaluations = 0;

		// Performs the required evaluations
		while (evaluations < maxEvaluations)
		{
			SolutionSet neighbors;
			
			//Not found  better solution and No restart
			if(findBetterEvaluations >= totalEvaluationsWithNoBetterSolution & !randomRestart)
				break;
			
			//Not found  better solution and restart
			if(findBetterEvaluations >= totalEvaluationsWithNoBetterSolution & randomRestart)
			{
				//Save the best solution until here
				if (comparator.compare(bestIndividual, lastGlobalBest) < 0)
					lastGlobalBest = bestIndividual;
				
				//new random solution
				Solution randomNewSolution = new Solution(problem);
				neighbors = visitor.execute(randomNewSolution);
				bestIndividual = neighbors.get(0); // First random neighbor
				//clear the counter
				findBetterEvaluations = 0;
				//System.out.println("aleatório : " + randomNewSolution);
			}
			else
			{
				neighbors = visitor.execute(bestIndividual);
				//System.out.println("normal : " + bestIndividual);
			}
			
			//Search in neighbors for a better solution
			for (int i = 0; i < neighbors.size(); i++)
			{
				Solution neighbor = neighbors.get(i);
				problem.evaluate(neighbor);
				evaluations++;
				
				if (comparator.compare(neighbor, bestIndividual) < 0)
					bestIndividual = neighbor;
				
			}
			
			if(!lastBest.equals(bestIndividual))
			{
				findBetterEvaluations = 0;
				lastBest = bestIndividual;
			}
			else
				findBetterEvaluations +=1;
			
		}

		// Return a population with the best individual
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(lastGlobalBest);
		return resultPopulation;
	}
}