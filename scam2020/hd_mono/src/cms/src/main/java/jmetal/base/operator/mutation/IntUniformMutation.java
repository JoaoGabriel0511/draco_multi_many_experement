/**
 * UniformMutation.java
 * Class representing a uniform mutation operator
 * @author Antonio J.Nebro
 * @version 1.0
 */
package jmetal.base.operator.mutation;

import java.util.Properties;
import jmetal.base.Solution;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XInt;

/**
 * This class implements a uniform mutation operator. NOTE: the type of the
 * solutions must be <code>SolutionType_.Real</code>
 */
public class IntUniformMutation extends Mutation
{
	/**
	 * REAL_SOLUTION represents class jmetal.base.solutionType.RealSolutionType
	 */
	private static Class<?> INT_SOLUTION;

	/**
	 * REAL_SOLUTION represents class
	 * jmetal.base.solutionType.ArrayRealSolutionType
	 */
	private static Class<?> ARRAY_INT_SOLUTION;

	/**
	 * Constructor Creates a new uniform mutation operator instance
	 */
	public IntUniformMutation()
	{
		try
		{
			INT_SOLUTION = Class.forName("jmetal.base.solutionType.IntSolutionType");
			ARRAY_INT_SOLUTION = Class.forName("jmetal.base.solutionType.ArrayIntSolutionType");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Constructor Creates a new uniform mutation operator instance
	 */
	public IntUniformMutation(Properties properties)
	{
		this();
	}

	/**
	 * Performs the operation
	 * 
	 * @param probability Mutation probability
	 * @param solution The solution to mutate
	 * @throws JMException
	 */
	public void doMutation(double probability, Solution solution) throws JMException
	{
		XInt x = new XInt(solution);

		for (int var = 0; var < solution.getDecisionVariables().length; var++)
		{
			if (PseudoRandom.randDouble() < probability)
			{
				double low = x.getLowerBound(var);
				double high = x.getUpperBound(var);
				double rand = low + PseudoRandom.randDouble() * (high - low);
				x.setValue(var, (int)rand);
			}
		}
	}

	/**
	 * Executes the operation
	 * 
	 * @param object An object containing the solution to mutate
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException
	{
		Solution solution = (Solution) object;

		if ((solution.getType().getClass() != INT_SOLUTION) && (solution.getType().getClass() != ARRAY_INT_SOLUTION))
		{
			Configuration.logger_.severe("IntUniformMutation.execute: the solution is not of the right type. The type should be 'Real', but " + solution.getType() + " is obtained");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Double probability = (Double) getParameter("probability");
		
		if (probability == null)
		{
			Configuration.logger_.severe("UniformMutation.execute: probability not specified");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		doMutation(probability.doubleValue(), solution);
		return solution;
	}
}