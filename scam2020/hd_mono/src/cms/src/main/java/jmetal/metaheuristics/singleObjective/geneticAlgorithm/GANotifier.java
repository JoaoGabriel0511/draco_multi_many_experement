package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.base.Solution;

public interface GANotifier
{
	void newIteration(int number, Solution best);
}