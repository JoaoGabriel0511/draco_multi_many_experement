/**
 * BestSolutionSelection.java
 * @author Antonio J. Nebro
 * @version 1.0
 */
package jmetal.base.operator.selection;

import java.util.Comparator;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;

/**
 * This class implements a selection operator used for selecting the best
 * solution in a SolutionSet according to a given comparator
 */
public class BestSolutionSelection extends Selection
{

	// Comparator
	private Comparator<Solution> comparator_;

	/**
	 * Constructor
	 * 
	 * @param comparator
	 */
	public BestSolutionSelection(Comparator<Solution> comparator)
	{
		comparator_ = comparator;
	}

	/**
	 * Performs the operation
	 * 
	 * @param object Object representing a SolutionSet.
	 * @return the best solution found
	 */
	public Object execute(Object object)
	{
		SolutionSet solutionSet = (SolutionSet) object;

		if (solutionSet.size() == 0)
		{
			return null;
		}
		int bestSolution;

		bestSolution = 0;

		for (int i = 1; i < solutionSet.size(); i++)
		{
			if (comparator_.compare(solutionSet.get(i), solutionSet.get(bestSolution)) < 0)
				bestSolution = i;
		} // for

		return bestSolution;
	} // Execute
} // BestObjectiveSelection
