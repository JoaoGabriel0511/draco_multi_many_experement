package jmetal.base.visitor.neighborhood;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class BinaryNeighborVisitor
{
	private Problem problem;
	
	public BinaryNeighborVisitor(Problem problem) throws JMException
	{
		if (problem.variableType_.length != 1)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a single variable");
			throw new JMException("NeighborVisitor.execute: problems should have a single variable");
		}

		if (problem.variableType_[0] != Binary.class)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a single binary variable");
			throw new JMException("NeighborVisitor.execute: problems should have a single binary variable");
		}
		
		this.problem = problem;
	}
	
	public SolutionSet execute(Solution individual) throws JMException, ClassNotFoundException
	{
		Binary variable = (Binary) individual.getDecisionVariables()[0];
		SolutionSet neighbors = new SolutionSet(variable.getNumberOfBits()); 

		for (int i = 0; i < variable.getNumberOfBits(); i++)
		{
			Solution neighbor = Solution.getNewSolution(problem);
			Binary neighborVariable = (Binary) neighbor.getDecisionVariables()[0];
			
			for (int j = 0; j < variable.getNumberOfBits(); j++)
				neighborVariable.bits_.set(j, variable.getIth(j));
			
			neighborVariable.bits_.set(i, !variable.getIth(i));
			neighborVariable.decode();
			neighbors.add(neighbor);
		}

		return neighbors;
	}
}