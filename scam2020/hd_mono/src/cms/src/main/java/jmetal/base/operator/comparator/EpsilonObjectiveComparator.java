/**
 * EpsilonObjectiveComparator.java
 * 
 * @author Juan J. Durillo
 * @version 1.0
 */
package jmetal.base.operator.comparator;

import java.util.Comparator;
import jmetal.base.Solution;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on epsilon dominance over a given
 * objective function.
 */
public class EpsilonObjectiveComparator implements Comparator<Solution>
{
	/**
	 * Stores the objective index to compare
	 */
	private int objective_;

	/**
	 * Stores the eta value for epsilon-dominance
	 */
	private double eta_;

	/**
	 * Constructor.
	 * 
	 * @param nObj Index of the objective to compare.
	 * @param eta Value for epsilon-dominance.
	 */
	public EpsilonObjectiveComparator(int nObj, double eta)
	{
		objective_ = nObj;
		eta_ = eta;
	} // EObjectiveComparator

	/**
	 * Compares two solutions.
	 * 
	 * @param o1 Object representing the first <code>Solution</code>.
	 * @param o2 Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	 *         respectively.
	 */
	public int compare(Solution o1, Solution o2)
	{
		if (o1 == null)
			return 1;

		if (o2 == null)
			return -1;

		double objetive1 = o1.getObjective(objective_);
		double objetive2 = o2.getObjective(objective_);

		// Objetive implements comparable!!!
		if (objetive1 / (1 + eta_) < objetive2)
			return -1;

		if (objetive1 / (1 + eta_) > objetive2)
			return 1;

		return 0;
	}
}