package br.unb.cic.cms.runner.algorithm;

import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.multiobjective.CouplingProblem;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.operator.crossover.UniformCrossover;
import jmetal.base.operator.mutation.IntUniformMutation;
import jmetal.base.operator.selection.BinaryTournament;
import jmetal.metaheuristics.nsgaII.NSGAII;

public class NSGAIIFactory implements  AlgorithmFactory {
    @Override
    public Algorithm instance(Project project) throws Exception {
        CouplingProblem problem = new CouplingProblem(project);

        Operator crossover = new UniformCrossover();
        crossover.setParameter("probability", 1.0);

        Operator mutation = new IntUniformMutation();
        mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());

        Operator selection = new BinaryTournament();

        int population = 10 * project.getPackageCount();
        int evaluations = 200 * project.getPackageCount() * population;

        NSGAII algorithm = new NSGAII(problem);
        algorithm.setInputParameter("populationSize", population);
        algorithm.setInputParameter("maxEvaluations", evaluations);
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
        return algorithm;
    }
}
