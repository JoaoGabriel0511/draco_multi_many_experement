package br.unb.cic.cms.runner.algorithm;

import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.multiobjective.CouplingProblem;
import jmetal.base.Algorithm;

public class RandomSearchFactory implements AlgorithmFactory {
    private int maxEvaluations_ = 25000;
    @Override
    public Algorithm instance(Project project) throws Exception {
        CouplingProblem problem = new CouplingProblem(project);
        Algorithm algorithm = new jmetal.metaheuristics.randomSearch.RandomSearch(problem);

        algorithm.setInputParameter("maxEvaluations", maxEvaluations_);
        return algorithm;
    }
}
