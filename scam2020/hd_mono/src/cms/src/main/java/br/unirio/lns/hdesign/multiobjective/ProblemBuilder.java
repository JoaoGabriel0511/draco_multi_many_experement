package br.unirio.lns.hdesign.multiobjective;

import jmetal.base.Algorithm;

public interface ProblemBuilder<ProblemClass, InstanceClass>
{
	public Algorithm createAlgorithm(InstanceClass instance) throws Exception;
}