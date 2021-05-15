package br.unb.cic.cms.runner.algorithm;

import br.unirio.lns.hdesign.model.Project;
import br.unirio.lns.hdesign.multiobjective.CouplingProblem;
import br.unirio.lns.hdesign.multiobjective.ProblemBuilder;


/**
 * Factory class for instantiating the clustering algorithm.
 */
public class ClusteringProblemBuilder  implements ProblemBuilder<CouplingProblem, Project> {
    AlgorithmFactory factory;
    public ClusteringProblemBuilder(String algorithm) {
        switch (Algorithm.valueOf(algorithm)) {
            case RANDOM_SEARCH: factory = new RandomSearchFactory(); break;
            default: factory = new NSGAIIFactory();
        }
    }

    @Override
    public jmetal.base.Algorithm createAlgorithm(Project project) throws Exception
    {
        return factory.instance(project);
    }
}
