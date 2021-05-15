package br.unirio.lns.hdesign.multiobjective;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.operator.crossover.UniformCrossover;
import jmetal.base.operator.mutation.IntUniformMutation;
import jmetal.base.operator.selection.BinaryTournament;
import jmetal.metaheuristics.nsgaII.NSGAII;
import br.unirio.lns.hdesign.model.Project;

public class ClusteringProblemBuilder implements ProblemBuilder<CouplingProblem, Project>
{
	@Override
	public Algorithm createAlgorithm(Project instance) throws Exception
	{
		CouplingProblem problem = new CouplingProblem(instance);

		Operator crossover = new UniformCrossover();
		crossover.setParameter("probability", 1.0);

		Operator mutation = new IntUniformMutation();
		mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());

		Operator selection = new BinaryTournament();

		int population = 10 * instance.getPackageCount();
		int evaluations = 200 * instance.getPackageCount() * population;
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.setInputParameter("populationSize", population);
		algorithm.setInputParameter("maxEvaluations", evaluations);
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);
		return algorithm;
	}
}