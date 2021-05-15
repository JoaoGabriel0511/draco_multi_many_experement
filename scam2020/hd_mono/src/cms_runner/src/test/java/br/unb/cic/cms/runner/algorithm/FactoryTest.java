package br.unb.cic.cms.runner.algorithm;

import jmetal.metaheuristics.nsgaII.NSGAII;
import org.junit.Assert;
import org.junit.Test;

public class FactoryTest {
    @Test
    public void testNSGAIIFactory() {
        ClusteringProblemBuilder builder = new ClusteringProblemBuilder("NSGAII");
        Assert.assertTrue(builder.factory instanceof NSGAIIFactory);
    }

    @Test
    public void testRandomSearchFactory() {
        ClusteringProblemBuilder builder = new ClusteringProblemBuilder("RANDOM_SEARCH");
        Assert.assertTrue(builder.factory instanceof RandomSearchFactory);
    }

}
