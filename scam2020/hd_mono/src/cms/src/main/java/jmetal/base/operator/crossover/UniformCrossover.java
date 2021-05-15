/**
 * SinglePointCrossover.java
 * Class representing a single point crossover operator
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.1
 */
package jmetal.base.operator.crossover;

import java.util.Properties;
import jmetal.base.Solution;
import jmetal.base.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions. NOTE: the operator is applied to binary or integer solutions,
 * considering the whole solution as a single variable.
 */
public class UniformCrossover extends Crossover
{
	/**
	 * BINARY_SOLUTION represents class
	 * jmetal.base.solutionType.RealSolutionType
	 */
	private static Class<?> BINARY_SOLUTION;
	/**
	 * BINARY_REAL_SOLUTION represents class
	 * jmetal.base.solutionType.BinaryRealSolutionType
	 */
	private static Class<?> BINARY_REAL_SOLUTION;
	/**
	 * INT_SOLUTION represents class jmetal.base.solutionType.IntSolutionType
	 */
	private static Class<?> INT_SOLUTION;

	/**
	 * Constructor Creates a new instance of the single point crossover operator
	 */
	public UniformCrossover()
	{
		try
		{
			BINARY_SOLUTION = Class.forName("jmetal.base.solutionType.BinarySolutionType");
			BINARY_REAL_SOLUTION = Class.forName("jmetal.base.solutionType.BinaryRealSolutionType");
			INT_SOLUTION = Class.forName("jmetal.base.solutionType.IntSolutionType");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Constructor Creates a new instance of the single point crossover operator
	 */
	public UniformCrossover(Properties properties)
	{
		this();
	}

	/**
	 * Perform the crossover operation.
	 * 
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containig the two offsprings
	 * @throws JMException
	 */
	public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMException
	{
		Solution[] offSpring = new Solution[2];
		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);
		
		try
		{
			if (PseudoRandom.randDouble() < probability)
			{
				if ((parent1.getType().getClass() == BINARY_SOLUTION) || (parent1.getType().getClass() == BINARY_REAL_SOLUTION))
				{
					for (int i = 0; i < parent1.getDecisionVariables().length; i++)
					{
						for (int j = 0; j < ((Binary) parent1.getDecisionVariables()[i]).getNumberOfBits(); j++)
						{
							boolean bit1 = ((Binary)parent1.getDecisionVariables()[i]).bits_.get(j);
							boolean bit2 = ((Binary)parent2.getDecisionVariables()[i]).bits_.get(j);

							if (PseudoRandom.randDouble() > 0.5)
							{
								boolean swap = bit2;
								bit2 = bit1;
								bit1 = swap;
							}

							((Binary)offSpring[0].getDecisionVariables()[i]).bits_.set(j, bit1);
							((Binary)offSpring[1].getDecisionVariables()[i]).bits_.set(j, bit2);
						}
					}
				}
				else
				{
					for (int i = 0; i < parent1.numberOfVariables(); i++)
					{
						if (PseudoRandom.randDouble() > 0.5)
						{
							int valueX1 = (int) parent1.getDecisionVariables()[i].getValue();
							int valueX2 = (int) parent2.getDecisionVariables()[i].getValue();
							offSpring[0].getDecisionVariables()[i].setValue(valueX2);
							offSpring[1].getDecisionVariables()[i].setValue(valueX1);
						}
					}
				}
			}
		}
		catch (ClassCastException e1)
		{
			Configuration.logger_.severe("UniformCrossover.doCrossover: Cannot perfom UniformCrossover");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".doCrossover()");
		}
		return offSpring;
	}

	/**
	 * Executes the operation
	 * 
	 * @param object An object containing an array of two solutions
	 * @return An object containing an array with the offSprings
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException
	{
		Solution[] parents = (Solution[]) object;

		if (((parents[0].getType().getClass() != BINARY_SOLUTION) || (parents[1].getType().getClass() != BINARY_SOLUTION)) && ((parents[0].getType().getClass() != BINARY_REAL_SOLUTION) || (parents[1].getType().getClass() != BINARY_REAL_SOLUTION)) && ((parents[0].getType().getClass() != INT_SOLUTION) || (parents[1].getType().getClass() != INT_SOLUTION)))
		{
			Configuration.logger_.severe("SinglePointCrossover.execute: the solutions are not of the right type. The type should be 'Binary' or 'Int', but " + parents[0].getType() + " and " + parents[1].getType() + " are obtained");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Double probability = (Double) getParameter("probability");
		
		if (parents.length < 2)
		{
			Configuration.logger_.severe("SinglePointCrossover.execute: operator " + "needs two parents");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}
		else if (probability == null)
		{
			Configuration.logger_.severe("SinglePointCrossover.execute: probability " + "not specified");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Solution[] offSpring;
		offSpring = doCrossover(probability.doubleValue(), parents[0], parents[1]);

		for (int i = 0; i < offSpring.length; i++)
		{
			offSpring[i].setCrowdingDistance(0.0);
			offSpring[i].setRank(0);
		}
		
		return offSpring;
	}
}
