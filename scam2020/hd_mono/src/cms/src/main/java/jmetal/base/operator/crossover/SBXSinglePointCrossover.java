/**
 * SBXSinglePointCrossover
 * @author Antonio J. Nebro
 * @version 10
 * 
 * This class implements a crossover operator to be applied to 
 * ArrayRealAndBinarySolutionType objects. The mutation combines SBX 
 * and single point crossover
 */
package jmetal.base.operator.crossover;

import jmetal.base.Solution;
import jmetal.base.operator.mutation.Mutation;
import jmetal.base.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

public class SBXSinglePointCrossover extends Mutation
{
	/**
	 * EPS defines the minimum difference allowed between real values
	 */
	protected static final double EPS = 1.0e-14;

	/**
	 * ETA_M_DEFAULT_ defines a default index for mutation
	 */
	public static final double ETA_M_DEFAULT_ = 20.0;

	/**
	 * eta_c stores the distributino index for the SBX crossover
	 */
	public double eta_c_ = ETA_M_DEFAULT_;

	/**
	 * ARRAY_REAL_AND_BINARY_SOLUTION represents class
	 * jmetal.base.solutionType.ArrayRealAndBinarySolutionType
	 */
	private static Class<?> ARRAY_REAL_AND_BINARY_SOLUTION;

	/**
	 * Constructor
	 */
	public SBXSinglePointCrossover()
	{
		try
		{
			ARRAY_REAL_AND_BINARY_SOLUTION = Class.forName("jmetal.problems.bci5.ArrayRealAndBinarySolutionType");
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch
	} // Constructor

	/**
	 * Perform the crossover operation.
	 * 
	 * @param probability Crossover probability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containing the two offsprings
	 */
	public Solution[] doCrossover(Double realProbability, Double binaryProbability, Solution parent1, Solution parent2) throws JMException
	{

		Solution[] offSpring = new Solution[2];

		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);

		// SBX crossover
		double rand;
		double y1, y2, yL, yu;
		double c1, c2;
		double alpha, beta, betaq;
		double valueX1, valueX2;
		XReal x1 = new XReal(parent1);
		XReal x2 = new XReal(parent2);
		XReal offs1 = new XReal(offSpring[0]);
		XReal offs2 = new XReal(offSpring[1]);

		// int numberOfVariables = x1.size();

		if (PseudoRandom.randDouble() <= realProbability)
		{
			for (int i = 0; i < x1.size(); i++)
			{
				valueX1 = x1.getValue(i);
				valueX2 = x2.getValue(i);
				if (PseudoRandom.randDouble() <= 0.5)
				{
					if (Math.abs(valueX1 - valueX2) > EPS)
					{

						if (valueX1 < valueX2)
						{
							y1 = valueX1;
							y2 = valueX2;
						}
						else
						{
							y1 = valueX2;
							y2 = valueX1;
						} // if

						yL = x1.getLowerBound(i);
						yu = x1.getUpperBound(i);
						rand = PseudoRandom.randDouble();
						beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
						alpha = 2.0 - Math.pow(beta, -(eta_c_ + 1.0));

						if (rand <= (1.0 / alpha))
						{
							betaq = Math.pow((rand * alpha), (1.0 / (eta_c_ + 1.0)));
						}
						else
						{
							betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (eta_c_ + 1.0)));
						} // if

						c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
						beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
						alpha = 2.0 - Math.pow(beta, -(eta_c_ + 1.0));

						if (rand <= (1.0 / alpha))
						{
							betaq = Math.pow((rand * alpha), (1.0 / (eta_c_ + 1.0)));
						}
						else
						{
							betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (eta_c_ + 1.0)));
						} // if

						c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));

						if (c1 < yL)
							c1 = yL;

						if (c2 < yL)
							c2 = yL;

						if (c1 > yu)
							c1 = yu;

						if (c2 > yu)
							c2 = yu;

						if (PseudoRandom.randDouble() <= 0.5)
						{
							offs1.setValue(i, c2);
							offs2.setValue(i, c1);
						}
						else
						{
							offs1.setValue(i, c1);
							offs2.setValue(i, c2);
						} // if
					} // if
					else
					{
						offs1.setValue(i, valueX1);
						offs2.setValue(i, valueX2);
					} // if
				} // if
				else
				{
					offs1.setValue(i, valueX2);
					offs2.setValue(i, valueX1);
				} // else
			} // for
		} // if

		// Single point crossover

		if (PseudoRandom.randDouble() <= binaryProbability)
		{
			Binary binaryChild0 = (Binary) offSpring[0].getDecisionVariables()[1];
			Binary binaryChild1 = (Binary) offSpring[1].getDecisionVariables()[1];

			int totalNumberOfBits = binaryChild0.getNumberOfBits();

			// 2. Calcule the point to make the crossover
			int crossoverPoint = PseudoRandom.randInt(0, totalNumberOfBits - 1);

			// 5. Make the crossover;
			for (int i = crossoverPoint; i < totalNumberOfBits; i++)
			{
				boolean swap = binaryChild0.bits_.get(i);
				binaryChild0.bits_.set(i, binaryChild1.bits_.get(i));
				binaryChild1.bits_.set(i, swap);
			} // for
		} // if

		return offSpring;
	} // doCrossover

	@Override
	public Object execute(Object object) throws JMException
	{
		Solution[] parents = (Solution[]) object;

		if (((parents[0].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION) && (parents[1].getType().getClass() != ARRAY_REAL_AND_BINARY_SOLUTION)))
		{
			Configuration.logger_.severe("SBXSinglePointCrossover: the solution " + "type " + parents[0].getType() + " is not allowed with this operator");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if

		Double realProbability = (Double) getParameter("realProbability");
		if (realProbability == null)
		{
			Configuration.logger_.severe("SBXSinglePointCrossover: probability of the real component" + "not specified");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Double binaryProbability = (Double) getParameter("binaryProbability");
		if (binaryProbability == null)
		{
			Configuration.logger_.severe("SBXSinglePointCrossover: probability of the binary component" + "not specified");
			Class<?> cls = String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Double distributionIndex = (Double) getParameter("distributionIndex");
		if (distributionIndex != null)
		{
			eta_c_ = distributionIndex;
		} // if

		Solution[] offSpring;
		offSpring = doCrossover(realProbability, binaryProbability, parents[0], parents[1]);

		return offSpring;
	} // execute

} // SBXSinglePointCrossover

